/*------------------------------------------------------
    Author : www.webthemez.com
    License: Commons Attribution 3.0
    http://creativecommons.org/licenses/by/3.0/
---------------------------------------------------------  */

(function ($) {
    "use strict";
    var layoutMainApp = {

        initFunction: function () {
            /*MENU 
            ------------------------------------*/
            $('#main-menu').metisMenu();
        },

        initialization: function () {
            layoutMainApp.initFunction();

        }

    }
    
    // Initializing ///
    $(document).ready(function () {
        layoutMainApp.initFunction(); 
        var urlPath = window.location.pathname;
        $("#menu_div a").each(function(){
        	if($(this).attr("href").indexOf(urlPath)!=-1) {
        		$(this).attr("class", "active-menu");
        	}
        });
    });
}(jQuery));
