This is a Maven project so get that set up if you haven't already. You will also need to install Selenium and Chromedriver, this is needed for the screenshot taking. The methods of installing Selenium will differ based on your system. 

Currently this works on MongoDB. Once the application is running, go to localhost:6067 to view the interface. You can add user/site pairs and click Subscribe. Every 1 minute(this can be changed in Main.java:54) a browser is opened with all the sites added, takes a screenshot of each, and saves it to res/captures. You can enter a site url and click View previous screenshots and it will display all screenshots that have been taken of that site. 

You will need to change the project root dir in Constants.java. Create res/captures/.

To do:
Email has not been configured yet, a SMTP server needs to be set up. There is no user authentication, you just put in an email address(or anything really)

Right now both the client and server both run in the same application, this could possibly be changed but I don't know how necessary that is.

There is also not much input checking so don't do anything unexpected.



