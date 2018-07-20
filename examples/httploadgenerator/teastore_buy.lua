--[[
	teastore_browse profile. Sends cyclical requests to webui instance or front-end loadbalancer.
	Browses store and updates shopping carts.
	Does not perform any actions that would change the database.
--]]

--[[
	Global Variables. Initialized at load driver startup.
--]]
prefix = "http://10.1.1.1:8080/tools.descartes.teastore.webui/"
productviewcount = 30
postIndex = {3, 11, 13}


--[[
	Gets called at the beginning of each "call cycle", perform as much work as possible here.
	Initialize all global variables here.
	Note that math.random is already initialized using a fixed seed (5) for reproducibility.
--]]
function onCycle()
	userpostfix = 1 + math.random(90)
	calls = {
	"",
	"login",
	--[[[POST]--]]"loginAction?username=user"..userpostfix.."&password=password",
	--[[[POST]--]]"category?page=1&category=",
	"product?id=",
	--[[[POST]--]]"cartAction?addToCart=&productid=",
	"category?page=1&category=",
	"category?page=",
	--[[[POST]--]]"cartAction?addToCart=&productid=",
	"order",
	--[[[POST]--]]"cartAction?firstname=User&lastname=User&address1=Road&address2=City&cardtype=volvo&cardnumber=314159265359&expirydate=12/2050&confirm=Confirm",
	"profile",
	--[[[POST]--]]"loginAction?logout=",
	}
end

--[[
	Gets called with ever increasing callnums for each http call until it returns nil.
	Once it returns nil, onCycle() is called again and callnum is reset to 1 (Lua convention).
	
	Here, you can use our HTML helper functions for conditional calls on returned texts (usually HTML, thus the name).
	We offer:
	- html.getMatches( regex )
		Returns all lines in the returned text stream that match a provided regex.
	- html.extractMatches( prefixRegex, postfixRegex )
		Returns all matches that are preceeded by a prefixRegex match and followed by a postfixRegex match.
		The regexes must have one unique match for each line in which they apply.
	- html.extractMatches( prefixRegex, matchingRegex, postfixRegex )
		Variant of extractMatches with a matching regex defining the string that is to be extracted.
--]]
function onCall(callnum)
	if callnum == 2 then
		local categoryids = html.extractMatches("href=.*category.*?category=","\\d+","&page=1.")
		categoryid = categoryids[math.random(#categoryids)]
	elseif callnum == 5 then
		local productids = html.extractMatches("href=.*product.*?id=","\\d+",". ><img")
		productid = productids[math.random(#productids)]
		local pagecount = #html.getMatches(".*href=.*category.*?category=\\d+&page=\\d+.>\\d+</a></li>.*")
		page = math.random(pagecount)
	elseif callnum == 9 then
		local productids = html.extractMatches("href=.*product.*?id=","\\d+",". ><img")
		productid = productids[math.random(#productids)]
	end

	if calls[callnum] == nil then
		return nil
	elseif callnum == 4 then
		return "[POST]"..prefix..calls[callnum]..categoryid.."&number="..productviewcount
	elseif callnum == 5 then
		return prefix..calls[callnum]..productid
	elseif callnum == 6 then
		return "[POST]"..prefix..calls[callnum]..productid
	elseif callnum == 7 then
		return prefix..calls[callnum]..categoryid
	elseif callnum == 8 then
		return prefix..calls[callnum]..page.."&category="..categoryid
	elseif callnum == 9 then
		return "[POST]"..prefix..calls[callnum]..productid
	elseif isPost(callnum) then
		return "[POST]"..prefix..calls[callnum]
	else
		return prefix..calls[callnum]
	end
end

function isPost(index)
	for i = 1,#postIndex do
		if index == postIndex[i] then
			return true
		end
	end
	return false
end
