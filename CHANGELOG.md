**This update is not backwards compatible!**  
Please delete your `config.yml` when updating to this version.

**Changes**

 - The plugin will no longer associate a UUID with an IP address and use a timestamp instead to store when an entry should be removed.

**Fixes**

- Fix an issue where the expiry check sometimes removed valid addresses.