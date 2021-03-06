function HistoryMonitor() {
	const that = this;
	
	const cachedActions = new Map();
	const moreDaysLoaded = new Map();

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

	function getActionsForDay(target, date) {
		that.getActions(target, firstMomentOfDate(date), lastMomentOfDate(date));
	}

	function subscractDaysFromToday(days) {
		var date = new Date();
		date.setDate(date.getDate() - days);
		return date;
	}

	function allocateCachedActions(targetId) {
		var notLoaded = false;

		if(!cachedActions.has(targetId)) {
			notLoaded = true;
			cachedActions.set(targetId, new Array());
		}

		return notLoaded;
	}

	function loadActions(target) {
		var notLoaded = allocateCachedActions(target.id);
		if(notLoaded) {
			getActionsForDay(target, new Date());
			return;
		}
		
		that.actionsHandled(target.id, cachedActions.get(target.id), true);
	}

	function sortByDate(actions) {
		actions.sort(function(a, b) {
			return new Date(a.timestamp) - new Date(b.timestamp);
		});
	}

	function putInCache(targetId, actions) {
		var actionsInCache = cachedActions.get(targetId);
		if(!actionsInCache)
			actionsInCache = new Array();
			
		var updatedActionsInCache = actionsInCache.concat(actions);
		sortByDate(updatedActionsInCache);
		cachedActions.set(targetId, updatedActionsInCache);

		return updatedActionsInCache;
	}

	that.parseActionsNotification = function(actionsNotification) {
		var actions = actionsNotification.actions;
		var targetId = actionsNotification.targetId;

		var allActionsForTarget = putInCache(targetId, actions);
		that.actionsHandled(targetId, allActionsForTarget, false);
	}

	that.pushConfirmation = function(target, actor, description) {
		var action = new Object();
		action.type = "ActionInfo";
		action.actor = actor;
		action.timestamp = new Date().toISOString();
		action.description = description;

		var allActionsForTarget = putInCache(target.id, action);
		that.actionsHandled(target.id, allActionsForTarget, true);
	}

	that.pushNotification = function(notification) {
		var action = new Object();
		action.type = "ActionInfo";
		action.actor = notification.actor;
		action.timestamp = notification.timestamp;
		action.description = notification.description;

		var allActionsForTarget = putInCache(notification.targetId, action);
		that.actionsHandled(notification.targetId, allActionsForTarget, true);
	}

	that.loadHistoryForDay = function(target) {
		loadActions(target);
	}

	function getDaysLoaded(targetId) {
		if(!moreDaysLoaded.has(targetId))
			moreDaysLoaded.set(targetId,  0);
		var val = moreDaysLoaded.get(targetId) + 1;
		moreDaysLoaded.set(targetId, val);
		return val;
	}

	that.loadMoreHistory = function(target) {
		var daysLoaded = getDaysLoaded(target.id);

		var today = new Date();
		var targetDate = subscractDaysFromToday(daysLoaded);
		var from = firstMomentOfDate(targetDate);
		var to = lastMomentOfDate(targetDate);

		that.getActions(target, from, to);
	}
}

HistoryMonitor.prototype.getActions = function(target, from, to) {
	throw new Error("not binded");
}

HistoryMonitor.prototype.actionsHandled = function(targetId, actions, areCurrentActions) {
	throw new Error("not binded");
}

