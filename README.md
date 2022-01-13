# Codebase of ACT'22 Submission

This folder contains the codebase of four modules of our system (alias: Anon).

For anonymization reasons, we replaced our project name with Anon, and removed all comments in the codebase.

The build process of the codebase relies on a self-hosted Maven server.
We have to remove the IP address of the server to anonymize it. Therefore, you cannot build the project with a simple maven build command.

We will provide a buildable/non-anonymized version of the codebase upon request.

## Code Structure

The project is structured into different packages:

- Anon.client 
  - Anon.client.rulegeneration-core 
  - Anon.client.client-service
- Anon.controller 
  - Anon.controller.controller-core 
  - Anon.controller.controller-service
  
The Anon client software focuses on generating DDoS traffic filtering rules based on the user's input. The Anon controller software receives rules from Anon client and deploys rules either locally at its own SDN software or at other Anon controllers.
