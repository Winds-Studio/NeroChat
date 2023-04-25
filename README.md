<div align="center">
  <img src="https://github.com/ImNotSoftik/NeroChat/blob/master/logo.png" width="32%" height="32%"/>
  <h1>NeroChat</h1>
  <h3>Effective chat system with colored text, ignore, whisper and chat logging, regex filters, anti-spam for anarchic servers</h3>

</div>

## Features

- Players can customize their chat message using various prefixes. As an example putting an '>' before a chat message
  will turn the text green (green-text). (Note: Does not change the color of player names).
- Players can whisper other players (defaults to a purple color).
- Players can ignore another player (works for both world and whisper chat). Ignore lists are persistent between logins.
  Players can also un-ignore a previously ignored player.
- Players can toggle chat on/off.
- Full permission support so that you can limit who has access to what colors / features.
- Filtering messages in public and private chat with REGEX.
- Ability to turn on the stealth filtering mode when the spammer will think that his messages are sent.
- Automatic dot and capital letter.
- Ability to ignore case in regular expressions, which will eliminate filtration bypasses.
- Folia Support
- PlaceholderApi support
- Personal and global message customization
- Configure that does not reset with plugin updates and multi-language support (credits: https://github.com/xGinko/BetterWorldStats)
- Anti-Caps System 

## Source

NeroChat is a fork of PistonChat from PistonMaster aimed at fixing old bugs and adding new features such as anti-spam, message filtering with REGEX, public chat formatting support and more.

Original code: https://github.com/AlexProgrammerDE/PistonChat

## Popular servers running derivatives of this plugin

- 6g6s.org
- f3f5.online

## Player Commands

![image](https://user-images.githubusercontent.com/78680226/232307796-39f2b7a4-b53a-42ae-af0b-60d61cefcb72.png)


## Config options

Standard configuration for review

<details>
  <summary>config.yml</summary>

```yml

##############
#  Language  #
##############
Language:
  # The default language to be used if auto-lang is off or no matching language file was found.
  default-language: en_us
  # Enable / Disable locale based messages.
  auto-language: true

##########
#  Main  #
##########
Main:
  # Enable / Disable bstats metrics. Please don't turn it off, if it is not difficult.
  bstats-metrics: true
  # Enable / Disable notification of a new version of the plugin. It is recommended to turn this on.
  notify-updates: true
  # Enable/disable the display of the player's nickname color.
  display-nickname-color: true
  prefix: '[&2NeroChat&r] &6'
  # Defines the sender's name when sending messages from the server console.
  console-name: '[console]'
  # Change the format of messages in public chat.
  chat-format: <%player%&r>
  # The size of the ignore list in pages. It is not recommended to set more than 5.
  ignore-list-size: 9

##############
#  Prefixes  #
##############

# To use these prefixes you need additionally the nerochat.<COLORCODE>
# / indicates disabled!
Prefixes:
  GREEN: '>'
  BLUE: /
  RED: /
  AQUA: /
  GOLD: /
  YELLOW: /
  GRAY: /
  BLACK: /
  DARK_GREEN: /
  DARK_RED: /
  DARK_GRAY: /
  DARK_BLUE: /
  DARK_AQUA: /
  DARK_PURPLE: /
  LIGHT_PURPLE: /
  ITALIC: /
  UNDERLINE: /
  BOLD: /
  STRIKETHROUGH: /

#################
#  RegexFilter  #
#################

# Filtering chat messages using regular expressions.
# If you don't know how to create them, you can use ChatGPT
RegexFilter:
  PublicChat:
    # Outputs the player's name and regex when the message is canceled.
    Logs-Enabled: true
    # Do I inform the player that his message has not been sent? Doesn't work with silent mode.
    Player-Notify: true
    # The player will think he is sending messages, but in fact no one will see his messages.
    Silent-Mode: false
    # The search for matches will be case insensitive. Eliminates many regex bypasses with capslocks.
    Case-Insensitive: true
    # Regular expressions to which the messages in the chat should correspond.
    # The regular expression in the standard config allows only Russian and English letters + keyboard characters (not all).
    Allowed-Regex:
    - '[^\[\]A-Za-zА-Яа-яЁё0-9 !%.(){}?/+_,=-@№*&^#$\\>`|-]+'
  Whisper:
    # Outputs the player's name and regex when the message is canceled.
    Logs-Enabled: true
    # Do I inform the player that his message has not been sent? Doesn't work with silent mode.
    Player-Notify: true
    # The player will think he is sending messages, but in fact no one will see his messages.
    Silent-Mode: false
    # The search for matches will be case insensitive. Eliminates many regex bypasses with capslocks.
    Case-Insensitive: true
    # Regular expressions to which the messages in the chat should correspond.
    # The regular expression in the standard config allows only Russian and English letters + keyboard characters (not all).
    Allowed-Regex:
    - '[^\[\]A-Za-zА-Яа-яЁё0-9 !%.(){}?/+_,=-@№*&^#$\\>`|-]+'

########################
#  ReadableFormatting  #
########################

# Automatically puts a period at the end of a sentence and a capital letter at the beginning of a sentence.
ReadableFormatting:
  PublicChat: true
  Whisper: true

```

</details>

Color codes and prefixes can be disabled by replacing the contents with / e.g. GREEN: /
Usage of color codes and prefixes can be restricted by using permissions.
You can customize the appearance of whispers.

## Tested Minecraft Versions

- On all versions the plugin works correctly

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
