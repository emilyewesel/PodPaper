Original App Design Project - README Template
===

# Pod Paper

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
In this app, a user views a newspaper layout, in a jagged puzzle of headlines and descriptions. However, when a use clicks on an article, They will hear a podcast instead of listening to an article. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category: news/entertainment**
- **Mobile: for androids**
- **Story: A user can view news stories and listen ot them as podcasts**
- **Market: People who like to listen to podcasts**
- **Habit: The app is hopefully habit-forming, as people will revisit the app to see news**
- **Scope: The focus is relatively narrow, just listening to news stories**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Implement a recycler view or a cardview that has a list of stories with their titles and descriptions. 
* When a user clicks on a story, we move to a more detailed view and the user has the option to listen to a podcast
* Integration with the spotify API allows for podcasts
* When displaying a podcast, the page is aesthetically pleasing
* Include use of the camera

**Optional Nice-to-have Stories**

* User can login and logout
* Within their account, the user can get personalized recommendations. I can use item-item collaborative filtering to do this. 
* Within a recyler view, some but not all podcasts have descriptions
* User and like and comment on podcasts

### 2. Screen Archetypes

* The app opens and the user sees a display of multiple stories.
   * Using a cardview or a recycler view, the user will be able to see a variety of news stories, inclding titles, descriptions, and headings.
* A more detailed view of a podcast
   * After clicking on a podcast, the user sees a more detailed view of that podcast, and can plya the podcast if they choose to. 
   * In this screen, the user can also like and comment on the podcast. 
   

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Homepage of news stories
* Search for a news story

**Flow Navigation** (Screen to Screen)

* Front page of a newspaper
   * Click on a story to see the details
* A more detailed page of the podcast
   * User can click start on the podcast to listen to it

## Wireframes
<img src="Screen Shot 2021-07-07 at 10.27.48 AM.png" width=600>

## Week By Week Goals
Week 1: The most important goal of this week is to get the spotify API up and running, so that the app can display and play podcasts. This is the first goal of the app. If I finish this before the first week, then I will move on to the goals for week 2. 
- [x] User can log in and log out
- [x] User can view a reasonably-sized subset of examples using GET requests from the Spotify API

Week 2: Add a variety of useful features
- [x] User can click on a podcast for a more detailed view of information about the podcast
- [x] User can take pictures using the camera
- [x] These pictures can be saved with a podcast
- [x] User can double tap to like podcasts
- [x] Integrate external library for visual effects (Lottie)
- [x] Images fade in when displayed
- [x] Integration with Parse
- [x] User can log in and log out 

Week 3: Implement stretch features using more advanced algorithms
- [x] For you page: Use item-item filtering to predict what podcast a user wants to listen to
- [x] Design and compute custom scores to determine vectors for podcasts
- [x] Search: use tf-idf computations to determine the most relevant podcasts 
- [x] Use levenshtein edit distance to guess the intention of a mispelled word 
- [x] Populate the database with a more robust set of podcasts

Week 4: Additional Features
- [x] Decompose functions and improve the readability of the code

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 

### Models
There are going to be two main objects in this app: the podcasts and the users. The podcasts will be accessed through the spotify API, and they will contain the following critical pieces of information: the ability to play the podcast, the title, the description, and the photo. The user will contain the critical login information, as well as possibly some information about the podcasts that a user might want to listen to. 
### Networking
I will not be writing to the Spotify API in this project, so I only have to worry about GET requests. Maybe to integrate use of the camera, I can have a feature that allows someone to scan a spotify barcode. Or, I could ask the users to take pictures of themselves listening to the podcasts.
