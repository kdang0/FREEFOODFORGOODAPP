# FREEFOODAPP

## Introduction

Students all around campus have a very similar issue. If you are a sophomore, junior, senior, or graduate student, food is always an important part of the day. But, the majority of students on campus do not have a meal plan. This requires students to either bring food to campus or to buy food on campus with their own money. Furthermore, preparing breakfast, lunch, or dinner requires ample amounts of time during the day that many students simply do not have due to their large course load, homework, work, and clubs. This causes the issue that many students simply skip eating food due to the cost and time, or students spending exuberant amounts of money on food. 

One of the best parts about being on a college campus, however, is the fact that there is almost always free food somewhere during the day. The food usually comes from events, classes, departments, samples, and other events on campus throughout the term. The problem with free food is that most of the time it is difficult to communicate when and where free food exists, as it can happen randomly. 

Our goal for our app is to provide a place where students and staff can post about free food to the greater community in an easy to use, responsive, and accurate app. Moreover, the app should be able to allow users to post to a public feed the details of the food, a photo, and location, and allow other users to “upvote” posts that are accurate and good, and to allow users to say when the food is gone/not accurate. 

## Competitors

One competitor that is very similar to our proposed app is called “FeedShare” (https://play.google.com/store/apps/details?id=com.stackkedteam.feedshare&hl=en_US&gl=US). This app is currently only for North Eastern University students. Key features include users (anyone) is allowed to post a food location, users can mark if they are going, comment on the post, or flag the post, as well as the original poster can post a photo of the food. The feed also includes time since the post was posted. While this app does seem useful, there are features that are lacking and features that we do not think are necessary. For one, although the post tells you where free food is, it does not tell you how far each post is from you currently, nor does it give you the option to filter by distance. Further, we do think that a comments section is necessary but we think there should also be a way for people to vote/verify that the free food is correct. The report part of the post is great however and we should incorporate a similar feature in the post, as well as the UI looks really nice.

Another competitor is the app called “Campus Food” (https://play.google.com/store/apps/details?id=com.certainly_apps.campus_food). This app has the same concept as hours, but executes it completely differently. Specifically, it does not have a list of the food places but instead has a map with pins for where free food is located. Besides that it doesn’t seem to have any other details about events or ways to “up vote” it or report it. Visually this makes the app more intuitive and can help users understand where the food is, but overall the app is lacking and we think that having the feed of details is much more useful. We still intend on allowing the users to click on the view and see the location, but users first want to understand what the food is and other details before the location.

## Design Rundown

When a user opens the app, they are seen in the login page where they must log into their account through their username and password. If they don’t have an account already, they have an option to create one, and pressing “Register” will send them to another layout, which allows them to set one up by entering their email, desired username, and password. To confirm creation, the user will have to press “Sign Up”; a layout will pop up and tell the user they have successfully created the account. Once the user is logged into their account, they will be able to see posts made by both theirs and other people using the app. If the user has created a post, they will have an option to remove it by doing a long press on the post. The user can create a new post by using the action bar menu where there is a plus icon. There, the user can write the name of the event, location, time, brief description, and upload an image. Once the user is content with their post, they can press “Post”. The recyclerview will now update the newly uploaded post. When the user clicks on the event, they can see the details of the event and they have the option to either upvote or downvote as well as comment. Pressing “Comment” will send them to another layout where they are able to see comments made in that particular event and can comment. 

(PNGs are provided for reference)

### View comments of a post
![image](https://user-images.githubusercontent.com/73298064/198181908-7251426a-4c2c-49a7-931d-00e3ded349d8.png)

 ### Create post
 ![image](https://user-images.githubusercontent.com/73298064/198182074-947074c1-ad95-4977-b486-863de24f4261.png)

### Login
![image](https://user-images.githubusercontent.com/73298064/198182118-8fda0ba1-8ce1-4085-9407-d11b68acaa26.png)

### Registration
![image](https://user-images.githubusercontent.com/73298064/198182197-32095317-23d6-4221-9b87-12eac84b9b8c.png)

### Post
![image](https://user-images.githubusercontent.com/73298064/198182219-56fd87ff-fc39-4356-b53f-3204390d19b9.png)

### Dashboard that displays all posts in the app 
![image](https://user-images.githubusercontent.com/73298064/198182324-6a5f10e0-e5ac-4a0e-9ddc-bc3965102b79.png)



