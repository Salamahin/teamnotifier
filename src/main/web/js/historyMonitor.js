function HistoryMonitor() {
	const that = this;
	
	const cachedActions = [];
	const moreDaysLoaded = [];

	function lastMomentOfDate(date) {
		var d = new Date(date.getTime());

		d.setHours(23);
		d.setMinutes(59);
		d.setSeconds(59);
		d.setMilliseconds(999);

		return d;
	}

	function firstMomentOfDate(date) {
		var d = new Date(date.getTime());

		d.setHours(0);
		d.setMinutes(0);
		d.setSeconds(0);
		d.setMilliseconds(0);

		return d;
	}

	function getActionsForDay(date) {
		that.getActions(firstMomentOfDate(date), lastMomentOfDate(date));
	}

	function subscractDaysFromToday(days) {
		var date = new Date();
		date.setDate(date.getDate() - days);
		return date;
	}

	function allocateCachedActions(targetId) {
		var notLoaded = false;

		if(cachedActions[targetId] === undefined) {
			notLoaded = true;
			cachedActions[targetId] = [];
		}

		return notLoaded;
	}

	function loadActions(targetId) {
		var notLoaded = allocateCachedActions(targetId);
		if(notLoaded) {
			getActionsForDate(new Date());
			return;
		}
		
		that.actionsHandled(targetId, cachedActions[targetId]);
	}

	function sortByDate(actions) {
		actions.sort(function(a, b) {
			return new Date(a.timestamp) - new Date(b.timestamp);
		});
	}

	function putInCache(targetId, actions) {
		var actionsInCache = cachedActions[targetId];
		var updatedActionsInCache.concat(actions);
		sortByDate(updatedActionsInCache);
		cachedActions[targetId] = updatedActionsInCache;

		return updatedActionsInCache;
	}

	HistoryMonitor.prototype.parseActionsNotification = function(actionsNotification) {
		var actions = actionsNotification.actions;
		var targetId = actionsNotification.targetId;

		var allActionsForTarget = putInCache(targetId, actions);
		that.actionsHandled(targetId, allActionsForTarget);
	}

	HistoryMonitor.prototype.parseActionNotification = function(actionNotification) {
		var action = new Object();
		action.type = "ActionInfo";
		action.actor = actionsNotification.actor;
		action.timestamp = actionsNotification.timestamp;
		action.description = actionsNotification.description;

		var targetId = actionNotification.targetId;

		var allActionsForTarget = putInCache(targetId, action);
		that.actionsHandled(targetId, allActionsForTarget);
	}

	HistoryMonitor.prototype.loadHistoryForDay = function(targetId)
		loadActions(targetId);
	}

	function getDaysLoaded(targetId) {
		if(moreDaysLoaded[targetId] === undefined)
			moreDaysLoaded[targetId] = 0;
		return moreDaysLoaded[targetId]++;
	}

	HistoryMonitor.prototype.loadMoreHistory = function(targetId) {
		var daysLoaded = getDaysLoaded(targetId) + 1;
		
		var today = new Date();
		var targetDate = subscractDaysFromToday(daysLoaded);
		var from = firstMomentOfDate(targetDate);
		var to = lastMomentOfDate(targetDate);

		getActions(targetId, from, to);
	}
}

HistoryMonitor.prototype.getActions = function(targetId, from, to) {
	throw new Error("not binded");
}

HistoryMonitor.prototype.actionsHandled = function(targetId, actions) {
	throw new Error("not binded");
}

