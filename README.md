TwitterAround

=======================


 TwitterAround - This app, that I wrote in the 2008/09 time frame uses the twitter4j API and does the following:
        - Allows the admin to define Campaigns.
        - The Campaign defines keywords to look for, in tweeters' profile
        - Stores the tweeters in a local db using a Quartz scheduled job
        - Allows Admins to store tweets per campaign, or across all campaigns.
        - Allows the campaign to be configured to send out tweets at a certain frequency to the stored tweeters in the campaign, using another quartz scheduled job
        - The tweets appear in the sending tweeters timeline.

 For doing so, this application uses the following features:
        - Quartz for scheduling
        - Hibernate for persistence into MySQL
        - IceFaces for presentation
        - Several other ancillary projects like SimpleCaptcha and Spring Testing Framework.

