## CivRelay

Sends alerts to Discord channels.

### How to Use

1. Install Forge
1. Install the [latest `.jar`](https://github.com/Gjum/CivRelay/releases)
1. Create a webhook in the config screen of one of your Discord channels: (or ask an admin to create a webhook url for you)
    - Click the gear next to the channel name
    - Select `Webhooks` on the left
    - Click `Create Webhook`
    - Copy the `Webhook Url` at the bottom
1. Open the config screen:
    - From the start screen: `Mods`, select `CivRelay` on the left, then click `Config` at the bottom left
    - From the Escape menu: `Mods config`, select `CivRelay` on the left, then click `Config` at the bottom left
1. Create a new Filter
1. Paste the webhook url, enter the game address you play on, change other options if you like
1. Click `Save and close`

<!-- TODO screenshots -->

### Alert Formatting

Use `{"content":"..."}` to tell Discord this is a text message, for example:
```{"content":"`<timeUTC>` <player> etc..."}```
You can use [Embeds](https://discordapp.com/developers/docs/resources/channel#create-message) too.

All the `<...>` will be replaced by various alert information:

| Format key          | Replacement examples       |
|:--------------------|:---------------------------|
| \<timeLocal\>       | 23:45:56                   |
| \<timeUTC\>         | 18:45:56                   |
| \<event\>           | Snitch \| Login/out        |
| \<player\>          | ttk2                       |
| \<snitch\>          | MySnitch                   |
| \<group\>           | MyGroup                    |
| \<action\>          | Enter \| Login \| Logout   |
| \<actionText\>      | entered snitch at \| logged out \| ... |
| \<x\>               | -1234                      |
| \<rx\>              | -1230                      |
| \<y\>               | 56                         |
| \<ry\>              | 60                         |
| \<z\>               | -789                       |
| \<rz\>              | -790                       |
<!--| \<chatMsg\>         | Hello!                     |-->
<!--| \<snitchType\>      | Entry \| Logging           |-->
<!--| \<shortSnitchType\> | E \| L                     |-->
<!--| \<world\>           | world \| world_the_end     |-->

<!--Standard world names are replaced with their friendlier variants (see table).-->

[![Travis build Status](https://travis-ci.org/Gjum/CivRelay.svg?branch=master)](https://travis-ci.org/Gjum/CivRelay)

