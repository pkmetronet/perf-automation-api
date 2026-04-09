package com.performance.api.service;

import com.performance.api.model.*;
import com.performance.api.model.ThreadGroup;
import com.performance.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion;
import us.abstracta.jmeter.javadsl.core.threadgroups.DslDefaultThreadGroup;
import us.abstracta.jmeter.javadsl.core.threadgroups.BaseThreadGroup.ThreadGroupChild;
import us.abstracta.jmeter.javadsl.core.configs.DslVariables;
import us.abstracta.jmeter.javadsl.core.samplers.BaseSampler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

@Service
public class LoadTestEngineService {

    private static final Logger logger = LoggerFactory.getLogger(LoadTestEngineService.class);

    private final TestExecutionService testExecutionService;
    private final TestPlanVariableRepository variableRepository;
    private final ThreadGroupRepository threadGroupRepository;
    private final HttpRequestRepository httpRequestRepository;
    private final HttpHeaderRepository httpHeaderRepository;
    private final DataExtractorRepository dataExtractorRepository;
    private final ResponseAssertionRepository assertionRepository;

    public LoadTestEngineService(TestExecutionService testExecutionService,
                                 TestPlanVariableRepository variableRepository,
                                 ThreadGroupRepository threadGroupRepository,
                                 HttpRequestRepository httpRequestRepository,
                                 HttpHeaderRepository httpHeaderRepository,
                                 DataExtractorRepository dataExtractorRepository,
                                 ResponseAssertionRepository assertionRepository) {
        this.testExecutionService = testExecutionService;
        this.variableRepository = variableRepository;
        this.threadGroupRepository = threadGroupRepository;
        this.httpRequestRepository = httpRequestRepository;
        this.httpHeaderRepository = httpHeaderRepository;
        this.dataExtractorRepository = dataExtractorRepository;
        this.assertionRepository = assertionRepository;
    }

    public void executeTestPlanAsync(Long testExecutionId, Long testPlanId) {
        //CompletableFuture.runAsync(() -> {
            try {
                logger.info("Initiating async execution for testExecutionId: {}, testPlanId: {}", testExecutionId, testPlanId);
                runJMeterDslEngine(testExecutionId, testPlanId);
            } catch (Exception e) {
                logger.error("Unhandled exception in executeTestPlanAsync for executionId: {}. Error: {}", testExecutionId, e.getMessage(), e);
                testExecutionService.updateTestExecutionStatus(testExecutionId, "FAILED");
            }
        //});
    }

    private void runJMeterDslEngine(Long executionId, Long testPlanId) throws Exception {
        logger.info("Starting runJMeterDslEngine with inputs -> executionId: {}, testPlanId: {}", executionId, testPlanId);

        try {
            TestExecution execution = testExecutionService.getTestExecutionById(executionId)
                    .orElseThrow(() -> new RuntimeException("Execution record not found for ID: " + executionId));

            List<TestPlanVariable> globalVars = variableRepository.findByTestPlanId(testPlanId);
            logger.info("Fetched {} global variables for testPlanId: {}", globalVars != null ? globalVars.size() : 0, testPlanId);
            
            // Manual variable setup to avoid .vars(Map) version issues
            DslVariables dslVars = vars();
            if (globalVars != null) {
                globalVars.forEach(v -> {
                    logger.debug("Setting global variable -> {}: {}", v.getName(), v.getValue());
                    dslVars.set(v.getName(), v.getValue());
                });
            }

            List<ThreadGroup> dbThreadGroups = threadGroupRepository.findByTestPlanId(testPlanId);
            logger.info("Fetched {} thread groups for testPlanId: {}", dbThreadGroups != null ? dbThreadGroups.size() : 0, testPlanId);
            List<DslDefaultThreadGroup> dslThreadGroups = new ArrayList<>();

            for (ThreadGroup group : dbThreadGroups) {
                logger.info("Processing ThreadGroup -> id: {}, name: {}, threads: {}, loops: {}", group.getId(), group.getName(), group.getNumThreads(), group.getLoopCount());
                
                List<HttpRequest> requests = httpRequestRepository.findByThreadGroupIdOrderBySequenceOrderAsc(group.getId());
                logger.info("Fetched {} requests for ThreadGroup id: {}", requests != null ? requests.size() : 0, group.getId());
                
                // Map each request into an HTTP Sampler
                List<ThreadGroupChild> samplerChildrenList = requests.stream().<ThreadGroupChild>map(req -> {
                    logger.debug("Processing HttpRequest -> id: {}, name: {}, path: {}", req.getId(), req.getName(), req.getPath());
                    
                    List<HttpHeader> reqHeaders = httpHeaderRepository.findByHttpRequestId(req.getId());
                    List<DataExtractor> reqExtractors = dataExtractorRepository.findByHttpRequestId(req.getId());
                    List<ResponseAssertion> reqAssertions = assertionRepository.findByHttpRequestId(req.getId());

                    // Combine all children into one flat list to avoid varargs array nesting errors
                    List<BaseSampler.SamplerChild> samplerChildren = new ArrayList<>();
                    
                    // 1. Add Headers
                    samplerChildren.add(buildHeaderManagerAsChild(reqHeaders));
                    
                    // 2. Add Assertions
                    samplerChildren.addAll(buildDslAssertions(reqAssertions));
                    
                    // 3. Add Extractors
                    samplerChildren.addAll(buildDslExtractors(reqExtractors));

                    return httpSampler(req.getName(), req.getPath())
                            .method(req.getMethod())
                            .children(samplerChildren.toArray(new BaseSampler.SamplerChild[0]));
                }).collect(Collectors.toList());

                // // Using the direct factory method to avoid ".threadCount()" undefined errors
                // DslDefaultThreadGroup dslGroup = threadGroup(
                //         group.getName(), 
                //         group.getNumThreads(), 
                //         group.getLoopCount(), 
                //         samplerChildrenList.toArray(new ThreadGroupChild[0])
                // ).rampTo(group.getNumThreads(), Duration.ofSeconds(group.getRampUpTime()));

                // Use the fluent API as suggested by the DSL exception to correctly order ramp-up and iterations
                DslDefaultThreadGroup dslGroup = threadGroup(group.getName())
                        .rampTo(group.getNumThreads(), Duration.ofSeconds(group.getRampUpTime()))
                        .holdIterating(group.getLoopCount())
                        .children(samplerChildrenList.toArray(new ThreadGroupChild[0]));                
                
                dslThreadGroups.add(dslGroup);
            }

            // 1. Build the children list manually
            List<DslTestPlan.TestPlanChild> testPlanChildren = new ArrayList<>();

            // 2. Add vars
            testPlanChildren.add(dslVars);

            // 3. Add all thread groups
            testPlanChildren.addAll(dslThreadGroups);

            // 4. Add JTL writer
            String jtlPath = execution.getS3KeyResultTree() != null 
                    ? execution.getS3KeyResultTree() 
                    : "api/metrics/" + execution.getId() + ".jtl";
            logger.info("Setting JTL writer path to: {}", jtlPath);
            testPlanChildren.add(jtlWriter(jtlPath));

            // 5. Pass as a single array
            DslTestPlan testPlan = testPlan(
                    testPlanChildren.toArray(new DslTestPlan.TestPlanChild[0])
            );        

            logger.info("Executing JMeter DSL Test Plan now...");
            testPlan.run();
            logger.info("JMeter DSL Test Plan execution completed successfully for executionId: {}", executionId);
            
            testExecutionService.updateTestExecutionStatus(executionId, "COMPLETED");

        } catch (Exception e) {
            logger.error("Error occurred during runJMeterDslEngine execution! executionId: {}, testPlanId: {}", executionId, testPlanId);
            logger.error("Exception Details: ", e);
            testExecutionService.updateTestExecutionStatus(executionId, "FAILED");
            throw e; // Rethrow to ensure executeTestPlanAsync catches it as well
        }
    }

    private BaseSampler.SamplerChild buildHeaderManagerAsChild(List<HttpHeader> headers) {
        var manager = httpHeaders();
        if (headers != null) {
            headers.forEach(h -> manager.header(h.getName(), h.getValue()));
        }
        return (BaseSampler.SamplerChild) manager;
    }

    private List<BaseSampler.SamplerChild> buildDslAssertions(List<ResponseAssertion> assertions) {
        List<BaseSampler.SamplerChild> dslAssertions = new ArrayList<>();
        if (assertions == null || assertions.isEmpty()) {
            dslAssertions.add(
                responseAssertion()
                    .fieldToTest(DslResponseAssertion.TargetField.RESPONSE_CODE)
                    .containsSubstrings("200")
            );
            return dslAssertions;
        }

        for (ResponseAssertion assertion : assertions) {
            String type = assertion.getType() != null ? assertion.getType().toUpperCase() : "";
            switch (type) {
                case "STATUS_CODE":
                    dslAssertions.add(responseAssertion()
                        .fieldToTest(DslResponseAssertion.TargetField.RESPONSE_CODE)
                        .containsSubstrings(assertion.getExpectedValue()));
                    break;
                case "RESPONSE_BODY":
                    if ("CONTAINS".equalsIgnoreCase(assertion.getMatchingRule())) {
                        dslAssertions.add(responseAssertion().containsSubstrings(assertion.getExpectedValue()));
                    } else if ("MATCHES".equalsIgnoreCase(assertion.getMatchingRule())) {
                        dslAssertions.add(responseAssertion().matchesRegexes(assertion.getExpectedValue()));
                    }
                    break;
                case "JSON_PATH":
                    dslAssertions.add(jsonAssertion(assertion.getExpectedValue()));
                    break;
                default:
                    dslAssertions.add(responseAssertion()
                        .fieldToTest(DslResponseAssertion.TargetField.RESPONSE_CODE)
                        .containsSubstrings(assertion.getExpectedValue()));
                    break;
            }
        }
        return dslAssertions;
    }

    private List<BaseSampler.SamplerChild> buildDslExtractors(List<DataExtractor> extractors) {
        if (extractors == null) return new ArrayList<>();
        
        return extractors.stream().map(e -> {
            String type = e.getExtractorType() != null ? e.getExtractorType().toUpperCase() : "";
            if ("JSON".equalsIgnoreCase(type)) {
                return jsonExtractor(e.getVariableName(), e.getExpression());
            } else if ("BOUNDARY".equalsIgnoreCase(type)) {
                return boundaryExtractor(e.getVariableName(), "", "");
            } else {
                return regexExtractor(e.getVariableName(), e.getExpression());
            }
        }).collect(Collectors.toList());
    }
}