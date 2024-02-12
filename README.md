<div align="center">
  <img src="https://github.com/ImNotSoftik/NeroChat/blob/master/logo.png" width="32%" height="32%"/>
  <h1>NeroChat</h1>
  <h3>Effective chat system with colored text, ignore, whisper and chat logging, regex filters, anti-spam for anarchic servers</h3>

</div>

**An advanced chat plugin for survival/anarchy servers.**

## About
This plugin was created to be a recreation of ChatCo from the ground up.
A lot of chat plugins usually don't get any updates anymore or have been abandoned by the developers. 

NeroChat will always get updates and contributions are welcome!

## Source
NeroChat is a fork of PistonChat from PistonMaster aimed at fixing old bugs and adding new features such as anti-spam, message filtering with REGEX, public chat formatting support and more.

Original code: https://github.com/AlexProgrammerDE/PistonChat

## Features
* Green Text
* Private Messaging
* Auto Complete when you click on a players name just like on the server 2b2t.org
* Ignore Commands
* Lots of features that 2b2t's chat plugin has.
* Regex Filter + silent-mode
* Auto Format
* Caps Filter
* Configure that does not reset with plugin updates and multi-language support (credits: https://github.com/xGinko/BetterWorldStats)
  
This project is in active development, so if you have any feature requests or issues please submit them here on GitHub. PRs are welcome, too. :octocat:

## Servers using NeroChat:

Add your server in a pull request

6g6s.org, f3f5.org

## Config options

Standard configuration for review

<details>
  <summary>config.yml</summary>

```yml

##############
#  Language  #
##############
language:
  # The default language to be used if auto-lang is off or no matching language file was found.
  default-language: en_us
  # Enable / Disable locale based messages.
  auto-language: true

#############
#  General  #
#############
general:
  # Enable / Disable bstats metrics. Please don't turn it off, if it is not difficult.
  bstats-metrics: true
  # Enable/disable the display of the player's nickname color.
  display-nickname-color: true
  plugin-prefix: '[&2NeroChat&r] &6'
  # Defines the sender's name when sending messages from the server console.
  console-name: '[console]'
  # Change the format of messages in public chat.
  chat-format: <%player%&r>
  # The size of the ignore list in pages. It is not recommended to set more than 5.
  ignore-list-size: 9

##############
#  Prefixes  #
##############

# To use these you need to add the respective permission.
# EXAMPLE: Prefixes.BLUE -> nerochat.chatcolor.BLUE
prefixes:
  ITALIC: '*'
  BOLD: '**'
  GREEN: '>'

###########
#  Audit  #
###########
audit:
  regex-filter:
    public-chat:
      enable: false
      logging: false
      notify-player: true
      silent-mode: true
      case-sensitive: false
      # Prevents any message that starts with "This is a" and ends with "banned message"
      banned-regex:
      - ^This is a(.*)banned message
    whisper:
      enable: false
      logging: false
      notify-player: true
      silent-mode: true
      case-sensitive: false
      # Prevents any message that starts with "This is a" and ends with "banned message"
      banned-regex:
      - ^This is a(.*)banned message
  auto-format:
    # Automatically puts a period at the end of a sentence and a capital letter at the beginning of a sentence.
    enable: false
    # If there are these characters at the end of the sentence, the plugin will not automatically put a period.
    end-sentence-chars: .?!
    public-chat:
      auto-caps: true
      auto-dot: true
    whisper:
      auto-dot: true
      auto-caps: true
  caps-filter:
    # Automatic message formatting with a large number of capital letters.
    enable: false
    percentage: 50.0

```

</details>

Color codes and prefixes can be disabled by replacing the contents with / e.g. GREEN: /
Usage of color codes and prefixes can be restricted by using permissions.
You can customize the appearance of whispers.

## Statistics

![Graph](https://bstats.org/signatures/bukkit/NeroChat.svg)

## Warranty

The Software is provided "as is" and without warranties of any kind, express
or implied, including but not limited to the warranties of merchantability,
fitness for a particular purpose, and non-infringement. In no event shall the
Authors or copyright owners be liable for any claims, damages or other
liability, whether in an action in contract, tort or otherwise, arising from,
out of or in connection with the Software or the use or other dealings in the
Software.
