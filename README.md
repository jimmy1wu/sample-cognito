# Sample app using Open Liberty and Amazon Cognito deployed using Open Liberty Operator on Red Hat OpenShift

## Setting up an Amazon Cognito User Pool

You can skip this section if you already have an Amazon Cognito User Pool instance. Otherwise, follow these steps to create an Amazon Cognito User Pool for the purposes of running this sample.

<details><summary>View instructions</summary>

To begin, visit the https://console.aws.amazon.com/cognito/home URL, sign in to your AWS account if required, and click `Create user pool`. Most things can be left as default, but some user action is needed to configure/navigate the form items indicated in the red boxes.

### 1. Configure sign-in experience

![Configure sign-in experience](/assets/cognito_step1.png)

### 2. Configure security requirements

![Configure security requirements](/assets/cognito_step2.png)

### 3. Configure sign-up experience

![Configure sign-up experience](/assets/cognito_step3.png)

### 4. Configure message delivery

![Configure message delivery](/assets/cognito_step4.png)

### 5. Integrate your app

![Integrate your app](/assets/cognito_step5.png)

### 6. Review and create

![Review and create](/assets/cognito_step6.png)

</details>

## Configuring Liberty to use Amazon Cognito as an OIDC Provider

### Configuring the Discovery Endpoint

Navigate to your Amazon Cognito User Pool and copy the User Pool ID.

![User pool ID](/assets/cognito_id.png)

Now, open the [deploy.yaml](https://github.com/jimmy1wu/sample-cognito/blob/main/deploy/deploy.yaml) file and update the `discoveryEndpoint` with:

```
https://cognito-idp.<region>.amazonaws.com/<user-pool-id>/.well-known/openid-configuration
```

### Configuring the Client ID and Client Secret

Navigate to the app client's page by clicking on `App integration` and then the name of your client.

![Navigate to client app](/assets/cognito_navigate_to_clientapp.png)

Copy the Client ID and Client Secret.
![Client ID and Client Secret](/assets/cognito_clientId_and_clientSecret.png)

Now, open the [secret.yaml](https://github.com/jimmy1wu/sample-cognito/blob/main/deploy/secret.yaml) file and update the `oidc-clientId` and `oidc-clientSecret` with the results from:

```
echo -n <client-id> | base64
echo -n <client-secret> | base64
```

## Creating a user and assigning them a group

### Create user

![Create user](/assets/cognito_create_user.png)

![Create user form](/assets/cognito_create_user_form.png)

### Create group

![Create group](/assets/cognito_create_group.png)

![Create group form](/assets/cognito_create_group_form.png)

### Assign user to group

![Select user](/assets/cognito_select_user.png)

![Add user to group](/assets/cognito_add_user_to_group.png)

![Add user to group user](/assets/cognito_add_user_to_group_user.png)

## Deploying the application

To deploy the application, ensure you have OpenShift CLI installed and are logged into your OpenShift 4 cluster.

### Create a new project

```
oc new-project sample-cognito
```

### Build the image

```
oc process -f deploy/build.yaml | oc create -f -
oc start-build sample-cognito-buildconfig --from-dir=.
```

### Wait until the build is complete

```
oc get builds
```

### Deploying the secrets and the application

```
oc apply -f deploy/secret.yaml
oc apply -f deploy/deploy.yaml
```

## Configuring the Callback URL

### Getting the callback URL

Run:

```
oc get routes
```

Your response should look something like this:

```
NAME             HOST/PORT     PATH   SERVICES         PORT       TERMINATION        WILDCARD
sample-cognito   <host:port>          sample-cognito   9443-tcp   passthrough/None   None
```

Your callback URL will be in the format:

```
https://<host:port>/ibm/api/social-login/redirect/oidc
```

### Set the callback URL on Amazon Cognito

![Edit hosted UI](/assets/cognito_edit_hosted_ui.png)

Replace `https://localhost` with the callback URL from the previous step and save:

![Edit hosted UI callback](/assets/cognito_edit_hosted_ui_callback.png)

## Testing the application

Test your application by visiting `<host:port>` from the previous step. You should be redirected to the Amazon Cognito login page. Enter the email and password used to create the first user in the earlier step.

![Login](/assets/cognito_login.png)

You will be prompted to change its password the first time you log in.

![Change password](/assets/cognito_change_password.png)

After changing the password, you will be redirected back to the sample app which will display your username, access token, and id token.

![Hello](/assets/hello.png)

## Deleting the project

Run the following command to delete the project and its associated resources:

```
oc delete project sample-cognito
```