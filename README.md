# syndesis-rest integration tests

This repository contains integration tests for syndesis-rest module.

## Running tests

You may run the tests against a syndesis instance deployed on OpenShift as follows:

```bash
mvn clean install -Dtoken=<SYNDESIS_USER_TOKEN> -Dsyndesis.url=<SYNDESIS_INSTANCE_URL> -Dcredentials.file=<PATH_TO_CREDENTIALS_FILE>
```

## Credentials

These tests require access to third party services such as GitHub, Twitter, and Salesforce. You will need at least:

* a GitHub account
* a Salesforce account
* two Twitter accounts

You should configure credentials for these services in JSON file with following syntax (you need to replace *** with valid values):

```json
{
  "twitter_listen": {
    "service": "twitter",
    "properties": {
      "screenName": "************",
      "consumerKey": "*************************",
      "consumerSecret": "**************************************************",
      "accessToken": "**************************************************",
      "accessTokenSecret": "*********************************************"
    }
  },
  "twitter_talky": {
    "service": "twitter",
    "properties": {
      "screenName": "************",
      "consumerKey": "*************************",
      "consumerSecret": "**************************************************",
      "accessToken": "**************************************************",
      "accessTokenSecret": "*********************************************"
    }
  },
  "salesforce": {
    "service": "salesforce",
    "properties": {
      "instanceUrl": "https://developer.salesforce.com",
      "loginUrl": "https://login.salesforce.com",
      "clientId": "*************************************************************************************",
      "clientSecret": "*******************",
      "userName": "**********************",
      "password": "*********"
    }
  },
  "github": {
    "service": "github",
    "properties": {
      "login": "**********",
      "token": "****************************************"
    }
  }
}
```

