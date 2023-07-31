### ws_finance_carrierpayment_autopay ###
This is the readme file for --- ws_finance_carrierpayment_autopay.

Sample README file below.

### Introduction ###
TODO: Give a short introduction of your project. Let this section explain the objectives or the motivation behind this project.

# Getting Started
1. To run the application locally, you need to run redis locally and point to that container.
    docker run -p 6379:6379 -d redis
    
2. Edit the application.yml to point to the locally run redis container below.
      redis:
        host: localhost
        port: 6379
        password: ""
      cache:
        type: redis
        

# Getting Started
TODO: Guide developers through getting your code up and running on their system. In this section you can talk about:
1.	Installation process
2.	Software dependencies
3.	Latest releases
4.	API references

### Build and Test ###
TODO: Describe and show how to build and run the tests.

### Contribute ###
TODO: Explain how others can contribute to make your code better.

If you want to learn more about creating readme files; reference the following ( https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops).

