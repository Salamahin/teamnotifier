function View() {
    function jumpToAnchor(id) {
        window.location.hash = "#" + id;
    }

    this.jumpToEnvironments = function () {
        jumpToAnchor("environments");
    };

    this.jumpToHistory = function () {
        jumpToAnchor("history");
    };

    this.jumpToServerActions = function () {
        jumpToAnchor("server_actions");
    };

    this.jumpToResourceActions = function () {
        jumpToAnchor("resource_actions");
    };

    function jumpToEnvironmentOnFocusLost(modal) {
        modal.addEventListener('click', function () {
            jumpToEnvironment();
        }, false);

        modal.children[0].addEventListener('click', function (e) {
            e.stopPropagation();
        }, false);
    }

    function jumpToEnvironmentOnEsc() {
        document.addEventListener('keyup', function (e) {
            if (e.keyCode == 27) {
                jumpToEnvironment();
            }
        });
    }

    this.init = function () {
        jumpToEnvironmentOnEsc();
        jumpToEnvironmentOnFocusLost(document.querySelector("#resource_actions_modal"));
        jumpToEnvironmentOnFocusLost(document.querySelector("#server_actions_modal"));
        jumpToEnvironmentOnFocusLost(document.querySelector("#resource_actions_modal"));
        jumpToEnvironmentOnFocusLost(document.querySelector("#history_modal"));
    };
}


View.prototype.showServerStatus = function (server) {

};