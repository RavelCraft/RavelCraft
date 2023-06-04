# RavelCraft

All the code for RavelCraft

## Setup

Requirements:
* WaterdogPE proxy
* Velocity proxy
* Fabric backends (Install GeyserMC & CrossSwitch)

Installation steps:
1. Install all the software and run it once, then stop it.
2. Change all configs to your liking.
3. Set Velocity forwarding to modern & copy the secret to the RavelCraft config.
4. On Geyser config, set auth type to offline and enable proxy connections, not proxy protocol.

## TODO

- [ ] Commands
    - [ ] Proxy
        - [ ] broadcast
        - [ ] cracked
        - [ ] info
        - [ ] language
        - [ ] lobby (l and hub)
        - [ ] maintenance
        - [ ] motd
        - [ ] rank
        - [ ] send (send player to server)
        - [ ] server (tp to server)
        - [ ] tempban
        - [ ] kick
        - [ ] nick
        - [ ] whitelist
    - [ ] Server
        - [ ] home (sethome, home, delhome (user & admin), gethome (admin))
        - [ ] tpa (tpaccept, tpdeny)
        - [ ] enderchest (based on rank)
        - [ ] headitem
        - [ ] maintenance
        - [ ] miniblocks
        - [ ] npc management
        - [ ] perks
        - [ ] spawn
        - [ ] suicide
- [ ] Internals
    - [ ] Centralised user management
    - [ ] Website
    - [ ] Custom player list
    - [ ] Ping messages
    - [ ] Permissions (based on rank)
    - [ ] UUID management (Geyser & Java player support)
    - [ ] 1984
    - [ ] Cool death messages
    - [ ] Whitelist
    - [ ] Cracked players join
    - [x] Bedrock players need a "." on their names
    - [ ] PLAYER INDOCTRINATION: RULES & ANNOUNCEMENTS (:
    - [ ] Redirect players back to lobby on backend server kick if possible
    - [ ] Lobby
        - [ ] Spawn always at spawn, not at leave place
        - [ ] No take damage
        - [ ] No entity damage (paintings & whatnot) except if in creative
        - [ ] No interactions except door & gates except if in creative
        - [ ] No entity interactions except if in creative
        - [ ] No block breaking except if in creative
    - [ ] Edits
        - [ ] Join and leave messages (not joined game + joined network)
        - [ ] Invisible item frames (make and unmake invis)
        - [ ] Crop trampling remove
        - [ ] Shulker always drop 2 shells
        - [ ] No drop perks on death (dead and respawn)
        - [ ] Chat cool looking & stop message reporting
- [ ] Perks
    - [ ] Announce arrival
    - [ ] Arrow trail
    - [ ] Cape
    - [ ] Enderchest
    - [ ] Entity in a bucket
    - [ ] Firework launcher
    - [ ] Hats
    - [ ] Magic stick
    - [ ] Pet (use real entities?)
    - [ ] Player picker upper
    - [ ] Speed boost
    - [ ] Super (dog or cat)