# Tweetlytics

Implemented a real time analytics engine using tweets from twitter data stream for trending hashtags by country.

## Steps Involved

1. Fetched the streaming data from twitter hose and analyzed the data stream by using Apache Storm.
2. Implemented a Chef cookbook to automate installation of services like Storm, Zookeeper on EC2 servers running in AWS.
3. Developed custom Bolts and Spouts for Apache Storm for parsing the tweets and extracting the necessary information from the tweets.
4. Implemented MySQL as a backend database for the persistence of tweets summary along with longitute and latitude of the tewwt origin.
5. Developed a Tableau dashboard with custom filters for the visualization of the tweets persisted in the database.

## Tweetlytics in Action:

![alt Tweetlytics](https://s3-us-west-1.amazonaws.com/full-stack-projects/Proj+4.PNG "Tweetlytics in action")

## Technologies Used:

1. Apache Storm.
2. Apache Zookeper.
3. AWS.
4. Chef.
5. MySQL.
6. Twitter API.
7. Tableau.
