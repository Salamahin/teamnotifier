function HistoryMonitor() {
	const that = this;
	
	const cachedActions = [];
	

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

	function idToday(date) {
		return new Date().toDateString() == date.toDateString();
	}

	function allocateCachedActions(targetId, from) {
		var notLoaded = false;

		if(cachedActions[targetId] === undefined) {
			notLoaded = true;
			cachedActions[targetId] = [];
		}

		if(cachedActions[targetId][from] === undefined) {
			notLoaded = true;
			cachedActions[targetId][from] = [];
		}

		return notLoaded;
	}

	function getActionsForDate(targetId, date) {
		if(isToday(date)) {
			that.getActions(firstMomentOfDate(date), lastMomentOfDate(date));
			return;
		}

		var from = firstMomentOfDate(date);
		
		var notLoaded = allocateCachedActions(targetId, from);

		if(notLoaded) {
			var to = lastMomentOfDate(date);
			that.getActions(from, to);
			return;
		}

		return cachedActions[targetId][from];
	}

	HistoryMonitor.prototype.sortByDate = function(actions) {
		actions.sort(function(a, b) {
			return new Date(a.timestamp) - new Date(b.timestamp);
		});
	}

	HistoryMonitor.prototype.parseActionsNotification = function(actionsNotification) {
		var actions = actionsNotification.actions;
		var targetId = actionsNotification.targetId;
		var from = actionsNotification.from;

		that.sortByDate(actions);
		cachedActions[targetId][firstMomentOfDate(from)] = actions;

		that.actionsHandled(targetId, from, actions);
	}

	HistoryMonitor.prototype.parseActionNotification = function(actionNotification) {
		var action = new Object();
		action.type = "ActionInfo";
		action.actor = actionsNotification.actor;
		action.timestamp = actionsNotification.timestamp;
		action.description = actionsNotification.description;

		var from = firstMomentOfDate(actionsNotification.timestamp));
		var targetId = actionNotification.targetId;

		allocateCachedActions(targetId, from);
		
		var actions = cachedActions[targetId][from];
		actions.push(action);
		that.sortByDate(actions);

		that.actionsHandled(targetId, from, actions);
	}

	HistoryMonitor.prototype.loadHistorieForDay = function(targetId, daysBeforeToday) {
		getActionsForDate(targetId, subscractDaysFromToday(daysBeforeToday));
	}
}

HistoryMonitor.prototype.getActions = function(targetId, from, to) {
	throw new Error("not binded");
}

HistoryMonitor.prototype.actionsHandled = function(targetId, from, actions) {
	throw new Error("not binded");
}

