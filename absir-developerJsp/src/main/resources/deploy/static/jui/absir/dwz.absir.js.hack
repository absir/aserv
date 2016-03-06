###dwz.database.js
var archetypes = $table.find(".archetype");
					if(archetypes && archetypes.length > 0) {
						trTm = trArchetype(archetypes[0].innerHTML);
						archetypes.remove();
						
					} else {
						$table.find("tr:first th[type]").each(function(i){
							var $th = $(this);
							var field = {
								type: $th.attr("type") || "text",
								patternDate: $th.attr("dateFmt") || "yyyy-MM-dd",
								name: $th.attr("name") || "",
								defaultVal: $th.attr("defaultVal") || "",
								size: $th.attr("size") || "12",
								enumUrl: $th.attr("enumUrl") || "",
								lookupGroup: $th.attr("lookupGroup") || "",
								lookupUrl: $th.attr("lookupUrl") || "",
								lookupPk: $th.attr("lookupPk") || "id",
								suggestUrl: $th.attr("suggestUrl"),
								suggestFields: $th.attr("suggestFields"),
								postField: $th.attr("postField") || "",
								fieldClass: $th.attr("fieldClass") || "",
								fieldAttrs: $th.attr("fieldAttrs") || ""
							};
							fields.push(field);
						});
					}

function trHtml(fields){
				var html = '';
				$(fields).each(function(){
					html += tdHtml(this);
				});
				return trArchetype(html);
			}
			function trArchetype(archetype){
				return '<tr class="unitBox">'+archetype+'</tr>';
			}