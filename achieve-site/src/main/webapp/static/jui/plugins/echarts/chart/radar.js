define("echarts/chart/radar", ["require", "../component/base", "./base", "zrender/shape/Polygon", "../component/polar", "../config", "../util/ecData", "zrender/tool/util", "zrender/tool/color", "../util/accMath", "../chart"], function (e) {
    function t(e, t, a, o, s) {
        i.call(this, e, t, a, o, s), n.call(this), this.refresh(o)
    }

    var i = e("../component/base"), n = e("./base"), a = e("zrender/shape/Polygon");
    e("../component/polar");
    var o = e("../config"), s = e("../util/ecData"), r = e("zrender/tool/util"), l = e("zrender/tool/color");
    return t.prototype = {
        type: o.CHART_TYPE_RADAR, _buildShape: function () {
            this.selectedMap = {}, this._symbol = this.option.symbolList, this._queryTarget, this._dropBoxList = [], this._radarDataCounter = 0;
            for (var e, t = this.series, i = this.component.legend, n = 0, a = t.length; a > n; n++)t[n].type === o.CHART_TYPE_RADAR && (this.serie = this.reformOption(t[n]), this.legendHoverLink = t[n].legendHoverLink || this.legendHoverLink, e = this.serie.name || "", this.selectedMap[e] = i ? i.isSelected(e) : !0, this.selectedMap[e] && (this._queryTarget = [this.serie, this.option], this.deepQuery(this._queryTarget, "calculable") && this._addDropBox(n), this._buildSingleRadar(n), this.buildMark(n)));
            this.addShapeList()
        }, _buildSingleRadar: function (e) {
            for (var t, i, n, a, o = this.component.legend, s = this.serie.data, r = this.deepQuery(this._queryTarget, "calculable"), l = 0; l < s.length; l++)n = s[l].name || "", this.selectedMap[n] = o ? o.isSelected(n) : !0, this.selectedMap[n] && (o ? (i = o.getColor(n), t = o.getItemShape(n), t && (t.style.brushType = this.deepQuery([s[l], this.serie], "itemStyle.normal.areaStyle") ? "both" : "stroke", o.setItemShape(n, t))) : i = this.zr.getColor(l), a = this._getPointList(this.serie.polarIndex, s[l]), this._addSymbol(a, i, l, e, this.serie.polarIndex), this._addDataShape(a, i, s[l], e, l, r), this._radarDataCounter++)
        }, _getPointList: function (e, t) {
            for (var i, n, a = [], o = this.component.polar, s = 0, r = t.value.length; r > s; s++)n = null != t.value[s].value ? t.value[s].value : t.value[s], i = "-" != n ? o.getVector(e, s, n) : !1, i && a.push(i);
            return a
        }, _addSymbol: function (e, t, i, n, a) {
            for (var o, r = this.series, l = this.component.polar, h = 0, d = e.length; d > h; h++)o = this.getSymbolShape(this.deepMerge([r[n].data[i], r[n]]), n, r[n].data[i].value[h], h, l.getIndicatorText(a, h), e[h][0], e[h][1], this._symbol[this._radarDataCounter % this._symbol.length], t, "#fff", "vertical"), o.zlevel = this._zlevelBase + 1, s.set(o, "data", r[n].data[i]), s.set(o, "value", r[n].data[i].value), s.set(o, "dataIndex", i), s.set(o, "special", h), this.shapeList.push(o)
        }, _addDataShape: function (e, t, i, n, o, r) {
            var h = this.series, d = [i, this.serie], m = this.getItemStyleColor(this.deepQuery(d, "itemStyle.normal.color"), n, o, i), c = this.deepQuery(d, "itemStyle.normal.lineStyle.width"), p = this.deepQuery(d, "itemStyle.normal.lineStyle.type"), u = this.deepQuery(d, "itemStyle.normal.areaStyle.color"), V = this.deepQuery(d, "itemStyle.normal.areaStyle"), U = {
                zlevel: this._zlevelBase,
                style: {
                    pointList: e,
                    brushType: V ? "both" : "stroke",
                    color: u || m || ("string" == typeof t ? l.alpha(t, .5) : t),
                    strokeColor: m || t,
                    lineWidth: c,
                    lineType: p
                },
                highlightStyle: {
                    brushType: this.deepQuery(d, "itemStyle.emphasis.areaStyle") || V ? "both" : "stroke",
                    color: this.deepQuery(d, "itemStyle.emphasis.areaStyle.color") || u || m || ("string" == typeof t ? l.alpha(t, .5) : t),
                    strokeColor: this.getItemStyleColor(this.deepQuery(d, "itemStyle.emphasis.color"), n, o, i) || m || t,
                    lineWidth: this.deepQuery(d, "itemStyle.emphasis.lineStyle.width") || c,
                    lineType: this.deepQuery(d, "itemStyle.emphasis.lineStyle.type") || p
                }
            };
            s.pack(U, h[n], n, i, o, i.name, this.component.polar.getIndicator(h[n].polarIndex)), r && (U.draggable = !0, this.setCalculable(U)), U = new a(U), this.shapeList.push(U)
        }, _addDropBox: function (e) {
            var t = this.series, i = this.deepQuery(this._queryTarget, "polarIndex");
            if (!this._dropBoxList[i]) {
                var n = this.component.polar.getDropBox(i);
                n.zlevel = this._zlevelBase, this.setCalculable(n), s.pack(n, t, e, void 0, -1), this.shapeList.push(n), this._dropBoxList[i] = !0
            }
        }, ondragend: function (e, t) {
            var i = this.series;
            if (this.isDragend && e.target) {
                var n = e.target, a = s.get(n, "seriesIndex"), o = s.get(n, "dataIndex");
                this.component.legend && this.component.legend.del(i[a].data[o].name), i[a].data.splice(o, 1), t.dragOut = !0, t.needRefresh = !0, this.isDragend = !1
            }
        }, ondrop: function (t, i) {
            var n = this.series;
            if (this.isDrop && t.target) {
                var a, o, r = t.target, l = t.dragged, h = s.get(r, "seriesIndex"), d = s.get(r, "dataIndex"), m = this.component.legend;
                if (-1 === d)a = {
                    value: s.get(l, "value"),
                    name: s.get(l, "name")
                }, n[h].data.push(a), m && m.add(a.name, l.style.color || l.style.strokeColor); else {
                    var c = e("../util/accMath");
                    a = n[h].data[d], m && m.del(a.name), a.name += this.option.nameConnector + s.get(l, "name"), o = s.get(l, "value");
                    for (var p = 0; p < o.length; p++)a.value[p] = c.accAdd(a.value[p], o[p]);
                    m && m.add(a.name, l.style.color || l.style.strokeColor)
                }
                i.dragIn = i.dragIn || !0, this.isDrop = !1
            }
        }, refresh: function (e) {
            e && (this.option = e, this.series = e.series), this.backupShapeList(), this._buildShape()
        }
    }, r.inherits(t, n), r.inherits(t, i), e("../chart").define("radar", t), t
}), define("echarts/component/polar", ["require", "./base", "zrender/shape/Text", "zrender/shape/Line", "zrender/shape/Polygon", "zrender/shape/Circle", "zrender/shape/Ring", "../config", "zrender/tool/util", "../util/coordinates", "../util/accMath", "../util/smartSteps", "../component"], function (e) {
    function t(e, t, n, a, o) {
        i.call(this, e, t, n, a, o), this.refresh(a)
    }

    var i = e("./base"), n = e("zrender/shape/Text"), a = e("zrender/shape/Line"), o = e("zrender/shape/Polygon"), s = e("zrender/shape/Circle"), r = e("zrender/shape/Ring"), l = e("../config"), h = e("zrender/tool/util"), d = e("../util/coordinates");
    return t.prototype = {
        type: l.COMPONENT_TYPE_POLAR, _buildShape: function () {
            for (var e = 0; e < this.polar.length; e++)this._index = e, this.reformOption(this.polar[e]), this._queryTarget = [this.polar[e], this.option], this._createVector(e), this._buildSpiderWeb(e), this._buildText(e), this._adjustIndicatorValue(e), this._addAxisLabel(e);
            for (var e = 0; e < this.shapeList.length; e++)this.zr.addShape(this.shapeList[e])
        }, _createVector: function (e) {
            for (var t, i = this.polar[e], n = this.deepQuery(this._queryTarget, "indicator"), a = n.length, o = i.startAngle, s = 2 * Math.PI / a, r = this._getRadius(), l = i.__ecIndicator = [], h = 0; a > h; h++)t = d.polar2cartesian(r, o * Math.PI / 180 + s * h), l.push({vector: [t[1], -t[0]]})
        }, _getRadius: function () {
            var e = this.polar[this._index];
            return this.parsePercent(e.radius, Math.min(this.zr.getWidth(), this.zr.getHeight()) / 2)
        }, _buildSpiderWeb: function (e) {
            var t = this.polar[e], i = t.__ecIndicator, n = t.splitArea, a = t.splitLine, o = this.getCenter(e), s = t.splitNumber, r = a.lineStyle.color, l = a.lineStyle.width, h = a.show, d = this.deepQuery(this._queryTarget, "axisLine");
            this._addArea(i, s, o, n, r, l, h), d.show && this._addLine(i, o, d)
        }, _addAxisLabel: function (t) {
            for (var i, a, o, s, a, r, l, d, m, c, p = e("../util/accMath"), u = this.polar[t], V = this.deepQuery(this._queryTarget, "indicator"), U = u.__ecIndicator, g = this.deepQuery(this._queryTarget, "splitNumber"), y = this.getCenter(t), f = 0; f < V.length; f++)if (i = this.deepQuery([V[f], u, this.option], "axisLabel"), i.show) {
                if (o = {}, o.textFont = this.getFont(), o = h.merge(o, i), o.lineWidth = o.width, a = U[f].vector, r = U[f].value, d = f / V.length * 2 * Math.PI, m = i.offset || 10, c = i.interval || 0, !r)return;
                for (var _ = 1; g >= _; _ += c + 1)s = h.merge({}, o), l = p.accAdd(r.min, p.accMul(r.step, _)), s.text = this.numAddCommas(l), s.x = _ * a[0] / g + Math.cos(d) * m + y[0], s.y = _ * a[1] / g + Math.sin(d) * m + y[1], this.shapeList.push(new n({
                    zlevel: this._zlevelBase,
                    style: s,
                    draggable: !1,
                    hoverable: !1
                }))
            }
        }, _buildText: function (e) {
            for (var t, i, a, o, s, r, l, h = this.polar[e], d = h.__ecIndicator, m = this.deepQuery(this._queryTarget, "indicator"), c = this.getCenter(e), p = 0, u = 0, V = 0; V < m.length; V++)o = this.deepQuery([m[V], h, this.option], "name"), o.show && (l = this.deepQuery([o, h, this.option], "textStyle"), i = {}, i.textFont = this.getFont(l), i.color = l.color, i.text = "function" == typeof o.formatter ? o.formatter.call(this.myChart, m[V].text, V) : "string" == typeof o.formatter ? o.formatter.replace("{value}", m[V].text) : m[V].text, d[V].text = i.text, t = d[V].vector, a = Math.round(t[0]) > 0 ? "left" : Math.round(t[0]) < 0 ? "right" : "center", o.margin ? (r = o.margin, p = t[0] > 0 ? r : -r, u = t[1] > 0 ? r : -r, p = 0 === t[0] ? 0 : p, u = 0 === t[1] ? 0 : u, t = this._mapVector(t, c, 1)) : t = this._mapVector(t, c, 1.2), i.textAlign = a, i.x = t[0] + p, i.y = t[1] + u, s = o.rotate ? [o.rotate / 180 * Math.PI, t[0], t[1]] : [0, 0, 0], this.shapeList.push(new n({
                zlevel: this._zlevelBase,
                style: i,
                draggable: !1,
                hoverable: !1,
                rotation: s
            })))
        }, getIndicatorText: function (e, t) {
            return this.polar[e] && this.polar[e].__ecIndicator[t] && this.polar[e].__ecIndicator[t].text
        }, getDropBox: function (e) {
            var t, i, e = e || 0, n = this.polar[e], a = this.getCenter(e), o = n.__ecIndicator, s = o.length, r = [], l = n.type;
            if ("polygon" == l) {
                for (var h = 0; s > h; h++)t = o[h].vector, r.push(this._mapVector(t, a, 1.2));
                i = this._getShape(r, "fill", "rgba(0,0,0,0)", "", 1)
            } else"circle" == l && (i = this._getCircle("", 1, 1.2, a, "fill", "rgba(0,0,0,0)"));
            return i
        }, _addArea: function (e, t, i, n, a, o, s) {
            for (var r, l, h, d, m = this.deepQuery(this._queryTarget, "type"), c = 0; t > c; c++)l = (t - c) / t, s && ("polygon" == m ? (d = this._getPointList(e, l, i), r = this._getShape(d, "stroke", "", a, o)) : "circle" == m && (r = this._getCircle(a, o, l, i, "stroke")), this.shapeList.push(r)), n.show && (h = (t - c - 1) / t, this._addSplitArea(e, n, l, h, i, c))
        }, _getCircle: function (e, t, i, n, a, o) {
            var r = this._getRadius();
            return new s({
                zlevel: this._zlevelBase,
                style: {x: n[0], y: n[1], r: r * i, brushType: a, strokeColor: e, lineWidth: t, color: o},
                hoverable: !1,
                draggable: !1
            })
        }, _getRing: function (e, t, i, n) {
            var a = this._getRadius();
            return new r({
                zlevel: this._zlevelBase,
                style: {x: n[0], y: n[1], r: t * a, r0: i * a, color: e, brushType: "fill"},
                hoverable: !1,
                draggable: !1
            })
        }, _getPointList: function (e, t, i) {
            for (var n, a = [], o = e.length, s = 0; o > s; s++)n = e[s].vector, a.push(this._mapVector(n, i, t));
            return a
        }, _getShape: function (e, t, i, n, a) {
            return new o({
                zlevel: this._zlevelBase,
                style: {pointList: e, brushType: t, color: i, strokeColor: n, lineWidth: a},
                hoverable: !1,
                draggable: !1
            })
        }, _addSplitArea: function (e, t, i, n, a, o) {
            var s, r, l, h, d, m = e.length, c = t.areaStyle.color, p = [], m = e.length, u = this.deepQuery(this._queryTarget, "type");
            if ("string" == typeof c && (c = [c]), r = c.length, s = c[o % r], "polygon" == u)for (var V = 0; m > V; V++)p = [], l = e[V].vector, h = e[(V + 1) % m].vector, p.push(this._mapVector(l, a, i)), p.push(this._mapVector(l, a, n)), p.push(this._mapVector(h, a, n)), p.push(this._mapVector(h, a, i)), d = this._getShape(p, "fill", s, "", 1), this.shapeList.push(d); else"circle" == u && (d = this._getRing(s, i, n, a), this.shapeList.push(d))
        }, _mapVector: function (e, t, i) {
            return [e[0] * i + t[0], e[1] * i + t[1]]
        }, getCenter: function (e) {
            var e = e || 0;
            return this.parseCenter(this.zr, this.polar[e].center)
        }, _addLine: function (e, t, i) {
            for (var n, a, o = e.length, s = i.lineStyle, r = s.color, l = s.width, h = s.type, d = 0; o > d; d++)a = e[d].vector, n = this._getLine(t[0], t[1], a[0] + t[0], a[1] + t[1], r, l, h), this.shapeList.push(n)
        }, _getLine: function (e, t, i, n, o, s, r) {
            return new a({
                zlevel: this._zlevelBase,
                style: {xStart: e, yStart: t, xEnd: i, yEnd: n, strokeColor: o, lineWidth: s, lineType: r},
                hoverable: !1
            })
        }, _adjustIndicatorValue: function (t) {
            for (var i, n, a = this.polar[t], o = this.deepQuery(this._queryTarget, "indicator"), s = o.length, r = a.__ecIndicator, l = this._getSeriesData(t), h = a.boundaryGap, d = a.splitNumber, m = a.scale, c = e("../util/smartSteps"), p = 0; s > p; p++) {
                if ("number" == typeof o[p].max)i = o[p].max, n = o[p].min || 0; else {
                    var u = this._findValue(l, p, d, h);
                    n = u.min, i = u.max
                }
                !m && n >= 0 && i >= 0 && (n = 0), !m && 0 >= n && 0 >= i && (i = 0);
                var V = c(n, i, d);
                r[p].value = {min: V.min, max: V.max, step: V.step}
            }
        }, _getSeriesData: function (e) {
            for (var t, i, n, a = [], o = this.component.legend, s = 0; s < this.series.length; s++)if (t = this.series[s], t.type == l.CHART_TYPE_RADAR) {
                i = t.data || [];
                for (var r = 0; r < i.length; r++)n = this.deepQuery([i[r], t, this.option], "polarIndex") || 0, n != e || o && !o.isSelected(i[r].name) || a.push(i[r])
            }
            return a
        }, _findValue: function (e, t, i, n) {
            function a(e) {
                (e > o || void 0 === o) && (o = e), (s > e || void 0 === s) && (s = e)
            }

            var o, s, r, l;
            if (e && 0 !== e.length) {
                if (1 == e.length && (s = 0), 1 != e.length)for (var h = 0; h < e.length; h++)r = "undefined" != typeof e[h].value[t].value ? e[h].value[t].value : e[h].value[t], a(r); else {
                    l = e[0];
                    for (var h = 0; h < l.value.length; h++)a("undefined" != typeof l.value[h].value ? l.value[h].value : l.value[h])
                }
                var d = Math.abs(o - s);
                return s -= Math.abs(d * n[0]), o += Math.abs(d * n[1]), s === o && (0 === o ? o = 1 : o > 0 ? s = o / i : o /= i), {
                    max: o,
                    min: s
                }
            }
        }, getVector: function (e, t, i) {
            e = e || 0, t = t || 0;
            var n = this.polar[e].__ecIndicator;
            if (!(t >= n.length)) {
                var a, o = this.polar[e].__ecIndicator[t], s = this.getCenter(e), r = o.vector, l = o.value.max, h = o.value.min;
                if ("undefined" == typeof i)return s;
                switch (i) {
                    case"min":
                        i = h;
                        break;
                    case"max":
                        i = l;
                        break;
                    case"center":
                        i = (l + h) / 2
                }
                return a = l != h ? (i - h) / (l - h) : .5, this._mapVector(r, s, a)
            }
        }, isInside: function (e) {
            var t = this.getNearestIndex(e);
            return t ? t.polarIndex : -1
        }, getNearestIndex: function (e) {
            for (var t, i, n, a, o, s, r, l, h, m = 0; m < this.polar.length; m++) {
                if (t = this.polar[m], i = this.getCenter(m), e[0] == i[0] && e[1] == i[1])return {
                    polarIndex: m,
                    valueIndex: 0
                };
                if (n = this._getRadius(), o = t.startAngle, s = t.indicator, r = s.length, l = 2 * Math.PI / r, a = d.cartesian2polar(e[0] - i[0], i[1] - e[1]), e[0] - i[0] < 0 && (a[1] += Math.PI), a[1] < 0 && (a[1] += 2 * Math.PI), h = a[1] - o / 180 * Math.PI + 2 * Math.PI, Math.abs(Math.cos(h % (l / 2))) * n > a[0])return {
                    polarIndex: m,
                    valueIndex: Math.floor((h + l / 2) / l) % r
                }
            }
        }, getIndicator: function (e) {
            var e = e || 0;
            return this.polar[e].indicator
        }, refresh: function (e) {
            e && (this.option = e, this.polar = this.option.polar, this.series = this.option.series), this.clear(), this._buildShape()
        }
    }, h.inherits(t, i), e("../component").define("polar", t), t
}), define("echarts/util/coordinates", ["require", "zrender/tool/math"], function (e) {
    function t(e, t) {
        return [e * n.sin(t), e * n.cos(t)]
    }

    function i(e, t) {
        return [Math.sqrt(e * e + t * t), Math.atan(t / e)]
    }

    var n = e("zrender/tool/math");
    return {polar2cartesian: t, cartesian2polar: i}
});