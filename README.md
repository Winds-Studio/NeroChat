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
- PlaceHolderApi support
- Personal and global message customization

## Source

NeroChat is a fork of PistonChat from PistonMaster aimed at fixing old bugs and adding new features such as anti-spam, message filtering with REGEX, public chat formatting support and more.

Original code: https://github.com/AlexProgrammerDE/PistonChat

## Popular servers running derivatives of this plugin

- 6g6s.org

## Player Commands

- **/ignore {player}** - ignores or un-ignores the player.
- **/ignorelist** - prints all ignored players.
- **/unignoreall** - clears ignore list
- **/togglechat** - disables regular chatting for the player - NOT PERSISTENT.
- **/toggletells** - disables tells for the player - NOT PERSISTENT,


## Config options

Standard configuration for review

<details>
  <summary>config.yml</summary>

```yml

# To use these prefixes you need additionally the nerochat.<COLORCODE>
# / indicates disabled!
# This config is configured to be what 2b2t.org has.
bstats-metrics: true
notify-updates: true

whisper:
  from: '&d%player%&d whispers: %message%'
  to: '&dYou whisper to %player%&d: %message%'
hovertext: '&6Message &3%player%'

ignore: '&6Permanently ignoring %player%&6. This is saved in &4/ignorelist.'
unignore: '&6No longer permanently ignoring &3%player%'

chatformat: <%player%&r>
stripnamecolor: false

consolename: '[console]'

ignorelistsize: 9
prefixes:
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

RegexFilter:
  Chat:
    ConsoleNotify: true
    PlayerNotify: true
    SilentMode: false
    CaseInsensitive: true
    Allowed-Regex:
    - '[^A-Za-zА-Яа-яЁё0-9 !%()?>+-_,/:]+'
  Whisper:
    ConsoleNotify: true
    PlayerNotify: true
    SilentMode: false
    CaseInsensitive: true
    Allowed-Regex:
    - '[^A-Za-zА-Яа-яЁё0-9 !%()?>+-_,/:]+'

ReadableFormatting:
  PublicChat: true
  Whisper: true

```

</details>

Color codes and prefixes can be disabled by replacing the contents with / e.g. GREEN: /
Usage of color codes and prefixes can be restricted by using permissions.
You can customize the appearance of whispers.

## Tested Minecraft Versions

- 1.12.2

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
