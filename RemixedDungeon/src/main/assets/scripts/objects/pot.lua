---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by mike.
--- DateTime: 9/25/21 7:41 PM
---

local RPD = require "scripts/lib/commonClasses"

local object = require "scripts/lib/object"


return object.init{
    desc  = function ()
        return {

        }
    end,

    stepOn = function(self, object, hero)
        return true
    end,

    image = function(self, object)
        return 16 * 3 + object:level():objectsKind()
    end
}