# 🤫 PrivateStatus

![GitHub tag (with filter)](https://img.shields.io/github/v/tag/tinyoverflow/minecraft-privatestatus?style=flat-square&label=version)
![GitHub issues](https://img.shields.io/github/issues/tinyoverflow/minecraft-privatestatus?style=flat-square&label=issues)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/tinyoverflow/minecraft-privatestatus/main?style=flat-square&label=updated)
![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/tinyoverflow/minecraft-privatestatus/main?style=flat-square&label=codefactor)
![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/tinyoverflow/minecraft-privatestatus/maven.yml?style=flat-square&label=build)
[![bStats Servers](https://img.shields.io/bstats/servers/19291?style=flat-square&label=servers)][bstats-link]

With PrivateStatus you can hide the online status of your server from unwanted players. This is especially worthwhile for private servers with a whitelist to prevent this. This plugin only works on Paper 1.20+ and is not compatible with Spigot. This is because this plugin relies on the so called `ServerListPingEvent` which can only be cancelled when using Paper.

## Configuration

The configuration is in `plugins/PrivateStatus/config.yml` and looks like this. Please only edit the `expiration-minutes` option. The `known-addresses` section is managed by the plugin itself. The option `expiration-minutes` defines how long an address should be kept in the history. The plugin automatically checks every 10 minutes if entries expired. If so, it removes them.

```yaml
# The amount of minutes after which addresses will expire. Minimum: 10.
expiration-minutes: 1440

# A list of already known addresses.
known-addresses: {}
```

## Video Example (YouTube)

[![PrivateStatus Demo](http://img.youtube.com/vi/aRo1AMhukKs/maxresdefault.jpg)](https://youtu.be/aRo1AMhukKs)

## More

- [View on GitHub][github-link]
- [View on Modrinth][modrinth-link]
- [View on Hangar][hangar-link]
- [View on bStats][bstats-link]

[github-link]: https://github.com/tinyoverflow/minecraft-privatestatus
[modrinth-link]: https://modrinth.com/plugin/privatestatus
[hangar-link]: https://hangar.papermc.io/tinyoverflow/PrivateStatus
[bstats-link]: https://bstats.org/plugin/bukkit/PrivateStatus/19291
