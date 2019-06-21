package example;

// Make sure to execute on the command line: sudo ifconfig lo0 alias 171.111.11.1

import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.*;
import com.amazonaws.services.stepfunctions.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.stepfunctions.model.ExecutionListItem;
import com.amazonaws.services.stepfunctions.model.ListExecutionsRequest;
import com.amazonaws.services.stepfunctions.model.ListExecutionsResult;
import com.amazonaws.services.stepfunctions.model.ListStateMachinesRequest;
import com.amazonaws.services.stepfunctions.model.ListStateMachinesResult;
import com.amazonaws.services.stepfunctions.model.StateMachineListItem;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class StateMachine {
    private String stateMachineArn = "arn:aws:states:us-east-1:123456789012:stateMachine:mpstest4";
    private StartExecutionResult result;
    private String executionArn;
    private LambdaLogger logger;

        public Response start(String req, Context context) {
        StartExecutionRequest startExecutionRequest = new StartExecutionRequest();
        stateMachineArn = req;
        startExecutionRequest.setStateMachineArn(stateMachineArn);
        logger = context.getLogger();
        logger.log("stateMachineArn: "+stateMachineArn);
        logger.log("\n");
        logger.log("input parameter: "+req);
        logger.log("\n");

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);
        clientConfiguration.setSocketTimeout((int)TimeUnit.SECONDS.toMillis(70));
        clientConfiguration.setMaxConnections(100);
        //Use if there is a proxy used
        //clientConfiguration.setProxyHost("proxy.online.nl");
        //clientConfiguration.setProxyPort(8080);

            // Set credentials for sam localhost

            AWSSessionCredentials awsCreds = new AWSSessionCredentials() {
            public String getSessionToken() {
                return "1";
            }

            public String getAWSAccessKeyId() {
                return "1";
            }

            public String getAWSSecretKey() {
                return "1";
            }
        };

            // The following might be an alternative
            //AWSStaticCredentialsProvider awsBasicCredProv = new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummyKey", "dummySecret"));

            AWSStaticCredentialsProvider awsCredProv = new AWSStaticCredentialsProvider(awsCreds);

        ProfileCredentialsProvider credentialsProvider =
                new ProfileCredentialsProvider();
        try {
        //   credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles " +
                            "file. Please make sure that your credentials file is " +
                            "at the correct location (~/.aws/credentials), and is " +
                            "in valid format.",
                    e);
        }

        AWSStepFunctions client = AWSStepFunctionsClientBuilder.standard()
                .withCredentials(awsCredProv)
                .withClientConfiguration(clientConfiguration)
                // Make sure to execute on the command line: sudo ifconfig lo0 alias 171.111.11.1
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://171.111.11.1:8083/","us-east-1"))
                .build();

            // The following is an alternative for non local execution
            // AWSStepFunctionsAsync client = AWSStepFunctionsAsyncClientBuilder.defaultClient();

        logger.log("startExecutionRequest: "+startExecutionRequest);
        logger.log("\n");
        try {
            logger.log("startExecutionAsync now ");
            logger.log("\n");
            result = client.startExecution(startExecutionRequest);
            logger.log("now get executionArn");
            logger.log("\n");
            executionArn= result.getExecutionArn();
            logger.log("executionArn: "+ executionArn);
            logger.log("\n");
            logger.log("startExecutionAsync done");
            logger.log("\n");
            return new Response("200","stepFunctionTriggered", executionArn);
        }
        catch (Exception e) {
            logger.log("Exception while starting execution:"+ e);
            logger.log("\n");
            return  new Response("400","Error occured while executing Step Function", "");
        } }

        private static class Request{
        String stateMachineArn;

            public Request () {
                 }
            public Request (String stateMachineArnPar)
            {
                this.stateMachineArn = stateMachineArnPar;
            }
            public String getstateMachineArn()
            {
                return stateMachineArn;
            }
        }

        private class Response {
        private String statusCode;
        private String body;
        private String ExecutionArn;

        public Response(String statusCode, String body, String executionArn) {
            this.statusCode = statusCode;
            this.body = body;
            this.ExecutionArn = executionArn;
        }

        public String getStatusCode() {
            return statusCode;
        }

            public String getBody() {
                return body;
            }

            public String getExecutionArn() {
                return ExecutionArn;
            }

        }
}