$(function(){
    /*
     main vars and functions starts here.
     */
    // hard coded client id
	var clientId = 42;
    // show client id
    $("#clientId").html(clientId);
    
    // list object for saving rules that are successfully installed.
    var installedRules = [];

    // rendering function(s)
    var renderInstalledRuleList = function(){
        var html = '';
        for (var i = 0; i < installedRules.length; i++) {
            var r = installedRules[i];
            var ruleHTML = '<div class="installed-rule" data-rule-id="'+r.ruleId+'">';
            ruleHTML += '<pre>' + JSON.stringify(r,null,2) +'</pre>';
            ruleHTML += '<button type="button" class="btn btn-danger revoke-rule">Revoke</button>';
            ruleHTML += '</div>';
            html += ruleHTML;
        }
        $('#installed-rules-block').html(html);
    };
    
    /*
    btn binding starts here.
     */
    
    // new rule btn binding
	$("#new-rule").on('click', function(){
		$("#client-form").append($("#template-form-rule").html());
	});
    
    // delete new rule btn binding
    $("#client-form").on('click', '.delete-new-rule', function(){
        $(this).closest(".form-rule").remove();
    });
    
    // delete installed rule btn binding
    $("#installed-rules-block").on('click', '.revoke-rule', function () {
        // get rule id for this related block
        var ruleId = $(this).closest(".installed-rule").data("rule-id");
        
        // prep. data
        var data = {"clientId": clientId, "AnonRuleId": ruleId};
        var dataString = JSON.stringify(data);
        
        // define callback function
        var cb = function(result) {
            if (result.status == 'false') {
                alert("we cannot complete your request.");
            } else {
                // remove the old installed rule from installedRules
                var newInstalledRules = [];
                for (var i = 0; i < installedRules.length; i++) {
                    var rule = installedRules[i];
                    if (rule.ruleId != ruleId) {
                        newInstalledRules.push(rule);
                    }
                }
                // reset list
                installedRules = newInstalledRules;
                // render the page
                renderInstalledRuleList();
            }
        };
        
        
        // send ajax request
        $.ajax({
            type: "POST",
            url: "/Anon/client/delete/",
            contentType:"application/json; charset=utf-8",
            data: dataString,
            success: cb,
            dataType: "json"
        });

    });
    
	// submit btn binding
	$("#submit").on('click', function(){
		var rules = [];
		$(".form-rule").each(
			function(i, v){
				var rule = [];
				rule.push($(v).find(".form-control")[0].value);
				rule.push($(v).find(".form-control")[1].value);
				rules.push(rule);
			}
		);

		// ready to send
		var data = {"clientId": clientId, "action": "DENY", "ipRules": rules};
		dataString = JSON.stringify(data);

        // define callback function
        var cb = function(result) {
            if (result == null || result.ruleId == null || parseInt(result.ruleId) == -1) {
                alert("please check your input");
            } else {
                data.ruleId = result.ruleId;
                installedRules.push(data);
                // render the page
                renderInstalledRuleList();
            }
        };
		// now we make the request
		$.ajax({
			type: "POST",
			url: "/Anon/client/create/",
			contentType:"application/json; charset=utf-8",
			data: dataString,
			success: cb,
			dataType: "json"
		});

	});
});