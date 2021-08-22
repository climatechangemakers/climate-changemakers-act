package com.climatechangemakers.act.feature.issue.manager

import com.climatechangemakers.act.feature.issue.model.Issue
import com.climatechangemakers.act.feature.issue.model.TalkingPoint
import javax.inject.Inject

// TODO(kcianfarini) remove when we hit DB
class FakeIssueManager @Inject constructor() : IssueManager {

  override suspend fun getFocusIssue() = Issue(
    title = "This is a focus issue with a pretty long title for testing",
    talkingPoints = talkingPoints.shuffled(),
  )

  override suspend fun getUnfocusedIssues() = (1..(2..10).random()).map {
    Issue(title = "Issue #$it", talkingPoints = talkingPoints.shuffled())
  }
}

private val talkingPoints = listOf(
  TalkingPoint(
    "Ordered and Unordered Lists",
    """
      * This is an issue with markdown bullet lists
      * It can have many bullets
      * some of them short
      * some of them are very long with a lot of words. Being verbose isn't always the best, but sometimes necessary
      
        * some of the bullets are even indented 
        
          * and some of the indented ones even have children bullet points 
          
      1. Not all lists are unordered, some of them have numbers. 
      2. they can have many elements
      3. or just a few
    """.trimIndent(),
  ),
  TalkingPoint(
    "Italics, Bold, and Quotes",
    """
      This is content with _italics_ and **bolds** content. Bold content can be used to **emphasize importance** for
      certains pieces of text. Italics, on the other hand, is used to distinguish _stand-alone works or certain words
      from others within your writing_. 
      
      Sometimes, we need an inline quote of a resource we're citing. A quote looks like the following. 
      
      > Is mayonnaise an instrument?
    """.trimIndent(),
  ),
  TalkingPoint(
    "Links and Images",
    """
      Links are used to cite resources. [This link](https://github.com/climatechangemakers/climate-changemakers-act) brings the user to our github repo so that they may follow along
      with our progress. Sometimes, links aren't necessarily hyperlinks to other webpages. Instead, they might be images,
      like the one below. 
      
      ![image](https://wallpapercave.com/wp/GZblMNU.jpg)
    """.trimIndent(),
  ),
  TalkingPoint(
    "Headings and subheadings",
    """
      # This is a large heading
      
      This content pertains to the whole document
       
      ## Those headings can have children 
      
      This content only refers to content contains in h2 and below
      
      ### ...and so on
    """.trimIndent(),
  ),
)