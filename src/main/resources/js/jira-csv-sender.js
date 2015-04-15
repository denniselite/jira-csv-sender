AJS.$(document).ready(function(){
	AJS.$.getJSON(AJS.contextPath()+"/rest/api/2/project",function(result){
		result.forEach(function(project){
			AJS.$('#projectsList').append("<option>"+project.name+"</option>");
		});
	});
	AJS.$('#projectsList').auiSelect2();
	AJS.$('#projectsList').change(function(){
		AJS.$('#sendFormBtn').removeAttr("disabled");
		AJS.$('#projectName').val(AJS.$("#projectsList option:selected").text());
	});
});