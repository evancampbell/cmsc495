How to run this:

Install Maven

Install MongoDB

Download Chromedriver for Selenium. Make sure you check the version of Chrome you have installed, and get that same version of Chromedriver.

Set Chromedriver to your PATH, this will vary by what OS you use

Assuming you're using gmail, google how to set up an application password for it. This is necessary for sending email from the application.

Update src/main/resources/application.yml:
- admin should be your email address
- password can be anything (these are for logging in as an admin in the application)
- smtp:address should be your email address(has to be gmail)
- smtp:password should be the gmail application password you set up
- spring:resources:static-locations should be: file:/PROJECTPATH/src/main/resources, where PROJECTPATH is the absolute path of the project


make a folder named mongo in the top level of the project

make a folder named captures in the src/main/resources directory

open command line/terminal, run this: mongod --dbpath /PROJECTPATH/mongo

then in another window or tab run: mvn spring-boot:run

it should be running, point your browser to localhost:8080/login. Create a new user, login, and add a site. You can log in as an admin to start the automated screenshot taking. The interval is set to 1 minute. 




