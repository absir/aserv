$.excel_class = {
	workbook : null,
	analyWorkbook : function(setting, workbook) {
		if (typeof workbook == undefined) {
			workbook = $.excel_class.workbook;
		}
	},
	analySheet : function(setting, sheet) {
		var start = 0;
		var title = sheet.title;
		var titles = sheet.rows[title];
		for (; titles[start] == ""; start++)
			;

		var similars = [];
		var length = titles.length;
		for ( var column = start; column < length; column++) {
			$.excel_class._analySimilars(similars, setting.fields, sheet.rows,
					title, column);
			$.excel_class._analySimilars(similars, setting.trees, sheet.rows,
					title, column, $.excel_class._analySimilarTree);
		}

		var analy = {};
		var mapping = {};
		length = similars.length;
		for ( var i = 0; i < length; i++) {
			var sim = similars[i];
			var column = sim.column;
			if (!analy[column]) {
				var field = sim.field;
				if (mapping[field]) {
					if (setting.fields[field]) {
						continue;
					}

					var fields = mapping[field];
					equals = {
						column : column,
						equals : sim.equals
					};

					var sort = 0;
					var last = fields.length;
					for (; sort < last; sort++) {
						if (fields[sort].equals < equals.equals) {
							break;
						}
					}

					if (sort < last) {
						fields.splice(sort, 0, equals);

					} else {
						fields.push(equals);
					}

				} else {
					if (setting.fields[field]) {
						mapping[field] = [ column ];

					} else {
						mapping[field] = [ {
							column : column,
							equals : sim.equals
						} ];
					}
				}

				analy[column] = true;
			}
		}

		for ( var field in setting.trees) {
			var fields = mapping[field];
			if (fields) {
				length = fields.length;
				for ( var i = 0; i < length; i++) {
					fields[i] = fields[i].column;
				}
			}
		}

		return {
			start : start,
			mapping : mapping
		};
	},
	_analySimilars : function(similars, fields, rows, title, column, analyFunc) {
		var caption = rows[title][column];
		for ( var field in fields) {
			var similar = 0;
			var length = fields[field].length;
			for ( var i = 0; i < length; i++) {
				var match = fields[field][i];
				match = similar_text(caption, match, 1);
				if (similar < match) {
					similar = match;
				}
			}

			similar = {
				similar : similar,
				field : field,
				column : column
			};

			if (analyFunc) {
				analyFunc(similar, fields[field], rows, title, column);
			}

			if (similar.similar < 8) {
				continue;
			}

			var sort = 0;
			var last = similars.length;
			for (; sort < last; sort++) {
				if (similars[sort].similar < similar.similar) {
					break;
				}
			}

			if (sort < last) {
				similars.splice(sort, 0, similar);

			} else {
				similars.push(similar);
			}
		}
	},
	_analySimilarTree : function(similar, matchs, rows, title, column) {
		var equals = 0;
		var length = rows.length;
		var values = {};
		for ( var row = title; row < length; row++) {
			var value = rows[row][column];
			if (value == null || values[value]) {
				equals++;

			} else {
				if (value != "") {
					values[value] = true;
				}
			}
		}

		length -= title + 2;
		equals = length > 0 ? 32.0 * equals / length : 0;
		if (matchs.length <= 0) {
			equals *= 2.0;
		}

		similar.equals = equals;
		similar.similar += equals;
	},
};