define("echarts/chart/chord", ["require", "../component/base", "./base", "zrender/shape/Text", "zrender/shape/Line", "zrender/shape/Sector", "../util/shape/Ribbon", "../util/shape/Icon", "zrender/shape/BezierCurve", "../config", "../util/ecData", "zrender/tool/util", "zrender/tool/vector", "../data/Graph", "../layout/Chord", "../chart"], function (e) {
    "use strict";
    function t(e, t, a, o, s) {
        i.call(this, e, t, a, o, s), n.call(this), this.scaleLineLength = 4, this.scaleUnitAngle = 4, this.refresh(o)
    }

    var i = e("../component/base"), n = e("./base"), a = e("zrender/shape/Text"), o = e("zrender/shape/Line"), s = e("zrender/shape/Sector"), r = e("../util/shape/Ribbon"), l = e("../util/shape/Icon"), h = e("zrender/shape/BezierCurve"), d = e("../config"), c = e("../util/ecData"), m = e("zrender/tool/util"), p = e("zrender/tool/vector"), u = e("../data/Graph"), V = e("../layout/Chord");
    return t.prototype = {
        type: d.CHART_TYPE_CHORD, _init: function () {
            var e = this.series;
            this.selectedMap = {};
            for (var t = {}, i = {}, n = 0, a = e.length; a > n; n++)if (e[n].type === this.type) {
                var o = this.isSelected(e[n].name);
                this.selectedMap[e[n].name] = o, o && this.buildMark(n), this.reformOption(e[n]), t[e[n].name] = e[n]
            }
            for (var n = 0, a = e.length; a > n; n++)if (e[n].type === this.type)if (e[n].insertToSerie) {
                var s = t[e[n].insertToSerie];
                e[n]._referenceSerie = s
            } else i[e[n].name] = [e[n]];
            for (var n = 0, a = e.length; a > n; n++)if (e[n].type === this.type && e[n].insertToSerie) {
                for (var r = e[n]._referenceSerie; r && r._referenceSerie;)r = r._referenceSerie;
                i[r.name] && this.selectedMap[e[n].name] && i[r.name].push(e[n])
            }
            for (var l in i)this._buildChords(i[l]);
            this.addShapeList()
        }, _getNodeCategory: function (e, t) {
            return e.categories && e.categories[t.category || 0]
        }, _getNodeQueryTarget: function (e, t) {
            var i = this._getNodeCategory(e, t);
            return [t, i, e]
        }, _getEdgeQueryTarget: function (e, t, i) {
            return i = i || "normal", [t.itemStyle && t.itemStyle[i], e.itemStyle[i].chordStyle]
        }, _buildChords: function (e) {
            for (var t = [], i = e[0], n = function (e) {
                return e.layout.size > 0
            }, a = 0; a < e.length; a++) {
                var o = e[a];
                if (this.selectedMap[o.name]) {
                    var s;
                    o.data && o.matrix ? s = this._getSerieGraphFromDataMatrix(o, i) : o.nodes && o.links && (s = this._getSerieGraphFromNodeLinks(o, i)), s.filterNode(n, this), t.push(s), s.__serie = o
                }
            }
            if (t.length) {
                var r = t[0];
                if (!i.ribbonType) {
                    var l = i.minRadius, h = i.maxRadius, d = 1 / 0, c = -1 / 0;
                    r.eachNode(function (e) {
                        c = Math.max(e.layout.size, c), d = Math.min(e.layout.size, d)
                    });
                    var m = (h - l) / (c - d);
                    r.eachNode(function (e) {
                        var t = this._getNodeQueryTarget(i, e), n = this.query(t, "symbolSize");
                        e.layout.size = c === d ? n || d : n || (e.layout.size - d) * m + l
                    }, this)
                }
                var p = new V;
                p.clockWise = i.clockWise, p.startAngle = i.startAngle * Math.PI / 180, p.clockWise || (p.startAngle = -p.startAngle), p.padding = i.padding * Math.PI / 180, p.sort = i.sort, p.sortSub = i.sortSub, p.directed = i.ribbonType, p.run(t);
                var u = this.query(i, "itemStyle.normal.label.show");
                if (i.ribbonType) {
                    this._buildSectors(i, 0, r, i, t), u && this._buildLabels(i, 0, r, i, t);
                    for (var a = 0, U = 0; a < e.length; a++)this.selectedMap[e[a].name] && this._buildRibbons(e, a, t[U++], i);
                    i.showScale && this._buildScales(i, 0, r)
                } else {
                    this._buildNodeIcons(i, 0, r, i, t), u && this._buildLabels(i, 0, r, i, t);
                    for (var a = 0, U = 0; a < e.length; a++)this.selectedMap[e[a].name] && this._buildEdgeCurves(e, a, t[U++], i, r)
                }
                this._initHoverHandler(e, t)
            }
        }, _getSerieGraphFromDataMatrix: function (e, t) {
            for (var i = [], n = 0, a = [], o = 0; o < e.matrix.length; o++)a[o] = e.matrix[o].slice();
            for (var s = e.data || e.nodes, o = 0; o < s.length; o++) {
                var r = {}, l = s[o];
                l.rawIndex = o;
                for (var h in l)"name" === h ? r.id = l.name : r[h] = l[h];
                var d = this._getNodeCategory(t, l), c = d ? d.name : l.name;
                if (this.selectedMap[c] = this.isSelected(c), this.selectedMap[c])i.push(r), n++; else {
                    a.splice(n, 1);
                    for (var m = 0; m < a.length; m++)a[m].splice(n, 1)
                }
            }
            var p = u.fromMatrix(i, a, !0);
            return p.eachNode(function (e) {
                e.layout = {size: e.data.outValue}, e.rawIndex = e.data.rawIndex
            }), p.eachEdge(function (e) {
                e.layout = {weight: e.data.weight}
            }), p
        }, _getSerieGraphFromNodeLinks: function (e, t) {
            for (var i = new u(!0), n = e.data || e.nodes, a = 0, o = n.length; o > a; a++) {
                var s = n[a];
                if (s && !s.ignore) {
                    var r = this._getNodeCategory(t, s), l = r ? r.name : s.name;
                    if (this.selectedMap[l] = this.isSelected(l), this.selectedMap[l]) {
                        var h = i.addNode(s.name, s);
                        h.rawIndex = a
                    }
                }
            }
            for (var a = 0, o = e.links.length; o > a; a++) {
                var d = e.links[a], c = d.source, m = d.target;
                "number" == typeof c && (c = n[c], c && (c = c.name)), "number" == typeof m && (m = n[m], m && (m = m.name));
                var p = i.addEdge(c, m, d);
                p && (p.rawIndex = a)
            }
            return i.eachNode(function (e) {
                var i = e.data.value;
                if (null == i)if (i = 0, t.ribbonType)for (var n = 0; n < e.outEdges.length; n++)i += e.outEdges[n].data.weight || 0; else for (var n = 0; n < e.edges.length; n++)i += e.edges[n].data.weight || 0;
                e.layout = {size: i}
            }), i.eachEdge(function (e) {
                e.layout = {weight: null == e.data.weight ? 1 : e.data.weight}
            }), i
        }, _initHoverHandler: function (e, t) {
            var i = e[0], n = t[0], a = this;
            n.eachNode(function (e) {
                e.shape.onmouseover = function () {
                    n.eachNode(function (e) {
                        e.shape.style.opacity = .1, e.labelShape && (e.labelShape.style.opacity = .1, e.labelShape.modSelf()), e.shape.modSelf()
                    });
                    for (var i = 0; i < t.length; i++)for (var o = 0; o < t[i].edges.length; o++) {
                        var s = t[i].edges[o], r = a._getEdgeQueryTarget(t[i].__serie, s.data);
                        s.shape.style.opacity = .1 * a.deepQuery(r, "opacity"), s.shape.modSelf()
                    }
                    e.shape.style.opacity = 1, e.labelShape && (e.labelShape.style.opacity = 1);
                    for (var i = 0; i < t.length; i++) {
                        var l = t[i].getNodeById(e.id);
                        if (l)for (var o = 0; o < l.outEdges.length; o++) {
                            var s = l.outEdges[o], r = a._getEdgeQueryTarget(t[i].__serie, s.data);
                            s.shape.style.opacity = a.deepQuery(r, "opacity");
                            var h = t[0].getNodeById(s.node2.id);
                            h && (h.shape && (h.shape.style.opacity = 1), h.labelShape && (h.labelShape.style.opacity = 1))
                        }
                    }
                    a.zr.refreshNextFrame()
                }, e.shape.onmouseout = function () {
                    n.eachNode(function (e) {
                        e.shape.style.opacity = 1, e.labelShape && (e.labelShape.style.opacity = 1, e.labelShape.modSelf()), e.shape.modSelf()
                    });
                    for (var e = 0; e < t.length; e++)for (var o = 0; o < t[e].edges.length; o++) {
                        var s = t[e].edges[o], r = [s.data, i];
                        s.shape.style.opacity = a.deepQuery(r, "itemStyle.normal.chordStyle.opacity"), s.shape.modSelf()
                    }
                    a.zr.refreshNextFrame()
                }
            })
        }, _buildSectors: function (e, t, i, n) {
            var a = this.parseCenter(this.zr, n.center), o = this.parseRadius(this.zr, n.radius), r = n.clockWise, l = r ? 1 : -1;
            i.eachNode(function (i) {
                var h = this._getNodeCategory(n, i.data), d = this.getColor(h ? h.name : i.id), m = i.layout.startAngle / Math.PI * 180 * l, p = i.layout.endAngle / Math.PI * 180 * l, u = new s({
                    zlevel: this.getZlevelBase(),
                    style: {
                        x: a[0],
                        y: a[1],
                        r0: o[0],
                        r: o[1],
                        startAngle: m,
                        endAngle: p,
                        brushType: "fill",
                        opacity: 1,
                        color: d,
                        clockWise: r
                    },
                    clickable: n.clickable,
                    highlightStyle: {brushType: "fill"}
                });
                u.style.lineWidth = this.deepQuery([i.data, n], "itemStyle.normal.borderWidth"), u.highlightStyle.lineWidth = this.deepQuery([i.data, n], "itemStyle.emphasis.borderWidth"), u.style.strokeColor = this.deepQuery([i.data, n], "itemStyle.normal.borderColor"), u.highlightStyle.strokeColor = this.deepQuery([i.data, n], "itemStyle.emphasis.borderColor"), u.style.lineWidth > 0 && (u.style.brushType = "both"), u.highlightStyle.lineWidth > 0 && (u.highlightStyle.brushType = "both"), c.pack(u, e, t, i.data, i.rawIndex, i.id, i.category), this.shapeList.push(u), i.shape = u
            }, this)
        }, _buildNodeIcons: function (e, t, i, n) {
            var a = this.parseCenter(this.zr, n.center), o = this.parseRadius(this.zr, n.radius), s = o[1];
            i.eachNode(function (i) {
                var o = i.layout.startAngle, r = i.layout.endAngle, h = (o + r) / 2, d = s * Math.cos(h), m = s * Math.sin(h), p = this._getNodeQueryTarget(n, i.data), u = this._getNodeCategory(n, i.data), V = this.deepQuery(p, "itemStyle.normal.color");
                V || (V = this.getColor(u ? u.name : i.id));
                var U = new l({
                    zlevel: this.getZlevelBase(),
                    z: 1,
                    style: {
                        x: -i.layout.size,
                        y: -i.layout.size,
                        width: 2 * i.layout.size,
                        height: 2 * i.layout.size,
                        iconType: this.deepQuery(p, "symbol"),
                        color: V,
                        brushType: "both",
                        lineWidth: this.deepQuery(p, "itemStyle.normal.borderWidth"),
                        strokeColor: this.deepQuery(p, "itemStyle.normal.borderColor")
                    },
                    highlightStyle: {
                        color: this.deepQuery(p, "itemStyle.emphasis.color"),
                        lineWidth: this.deepQuery(p, "itemStyle.emphasis.borderWidth"),
                        strokeColor: this.deepQuery(p, "itemStyle.emphasis.borderColor")
                    },
                    clickable: n.clickable,
                    position: [d + a[0], m + a[1]]
                });
                c.pack(U, e, t, i.data, i.rawIndex, i.id, i.category), this.shapeList.push(U), i.shape = U
            }, this)
        }, _buildLabels: function (e, t, i, n) {
            var o = this.query(n, "itemStyle.normal.label.color"), s = this.query(n, "itemStyle.normal.label.rotate"), r = this.query(n, "itemStyle.normal.label.distance"), l = this.parseCenter(this.zr, n.center), h = this.parseRadius(this.zr, n.radius), d = n.clockWise, c = d ? 1 : -1;
            i.eachNode(function (e) {
                var t = e.layout.startAngle / Math.PI * 180 * c, i = e.layout.endAngle / Math.PI * 180 * c, d = (t * -c + i * -c) / 2;
                d %= 360, 0 > d && (d += 360);
                var m = 90 >= d || d >= 270;
                d = d * Math.PI / 180;
                var u = [Math.cos(d), -Math.sin(d)], V = 0;
                V = n.ribbonType ? n.showScaleText ? 35 + r : r : r + e.layout.size;
                var U = p.scale([], u, h[1] + V);
                p.add(U, U, l);
                var g = {
                    zlevel: this.getZlevelBase() + 1,
                    hoverable: !1,
                    style: {
                        text: null == e.data.label ? e.id : e.data.label,
                        textAlign: m ? "left" : "right",
                        color: o || "#000000"
                    }
                };
                s ? (g.rotation = m ? d : Math.PI + d, g.style.x = m ? h[1] + V : -h[1] - V, g.style.y = 0, g.position = l.slice()) : (g.style.x = U[0], g.style.y = U[1]), g.style.textColor = this.deepQuery([e.data, n], "itemStyle.normal.label.textStyle.color") || "#fff", g.style.textFont = this.getFont(this.deepQuery([e.data, n], "itemStyle.normal.label.textStyle")), g = new a(g), this.shapeList.push(g), e.labelShape = g
            }, this)
        }, _buildRibbons: function (e, t, i, n) {
            var a = e[t], o = this.parseCenter(this.zr, n.center), s = this.parseRadius(this.zr, n.radius);
            i.eachEdge(function (l, h) {
                var d, m = i.getEdge(l.node2, l.node1);
                if (m && !l.shape) {
                    if (m.shape)return void(l.shape = m.shape);
                    var p = l.layout.startAngle / Math.PI * 180, u = l.layout.endAngle / Math.PI * 180, V = m.layout.startAngle / Math.PI * 180, U = m.layout.endAngle / Math.PI * 180;
                    d = this.getColor(1 === e.length ? l.layout.weight <= m.layout.weight ? l.node1.id : l.node2.id : a.name);
                    var g = this._getEdgeQueryTarget(a, l.data), y = this._getEdgeQueryTarget(a, l.data, "emphasis"), f = new r({
                        zlevel: this.getZlevelBase(),
                        style: {
                            x: o[0],
                            y: o[1],
                            r: s[0],
                            source0: p,
                            source1: u,
                            target0: V,
                            target1: U,
                            brushType: "both",
                            opacity: this.deepQuery(g, "opacity"),
                            color: d,
                            lineWidth: this.deepQuery(g, "borderWidth"),
                            strokeColor: this.deepQuery(g, "borderColor"),
                            clockWise: n.clockWise
                        },
                        clickable: n.clickable,
                        highlightStyle: {
                            brushType: "both",
                            opacity: this.deepQuery(y, "opacity"),
                            lineWidth: this.deepQuery(y, "borderWidth"),
                            strokeColor: this.deepQuery(y, "borderColor")
                        }
                    });
                    c.pack(f, a, t, l.data, null == l.rawIndex ? h : l.rawIndex, l.data.name || l.node1.id + "-" + l.node2.id, l.node1.id, l.node2.id), this.shapeList.push(f), l.shape = f
                }
            }, this)
        }, _buildEdgeCurves: function (e, t, i, n, a) {
            var o = e[t], s = this.parseCenter(this.zr, n.center);
            i.eachEdge(function (e, i) {
                var n = a.getNodeById(e.node1.id), r = a.getNodeById(e.node2.id), l = n.shape, d = r.shape, m = this._getEdgeQueryTarget(o, e.data), p = this._getEdgeQueryTarget(o, e.data, "emphasis"), u = new h({
                    zlevel: this.getZlevelBase(),
                    z: 0,
                    style: {
                        xStart: l.position[0],
                        yStart: l.position[1],
                        xEnd: d.position[0],
                        yEnd: d.position[1],
                        cpX1: s[0],
                        cpY1: s[1],
                        lineWidth: this.deepQuery(m, "width"),
                        strokeColor: this.deepQuery(m, "color"),
                        opacity: this.deepQuery(m, "opacity")
                    },
                    highlightStyle: {
                        lineWidth: this.deepQuery(p, "width"),
                        strokeColor: this.deepQuery(p, "color"),
                        opacity: this.deepQuery(p, "opacity")
                    }
                });
                c.pack(u, o, t, e.data, null == e.rawIndex ? i : e.rawIndex, e.data.name || e.node1.id + "-" + e.node2.id, e.node1.id, e.node2.id), this.shapeList.push(u), e.shape = u
            }, this)
        }, _buildScales: function (e, t, i) {
            var n, s, r = e.clockWise, l = this.parseCenter(this.zr, e.center), h = this.parseRadius(this.zr, e.radius), d = r ? 1 : -1, c = 0, m = -1 / 0;
            e.showScaleText && (i.eachNode(function (e) {
                var t = e.data.value;
                t > m && (m = t), c += t
            }), m > 1e10 ? (n = "b", s = 1e-9) : m > 1e7 ? (n = "m", s = 1e-6) : m > 1e4 ? (n = "k", s = .001) : (n = "", s = 1));
            var u = c / (360 - e.padding);
            i.eachNode(function (t) {
                for (var i = t.layout.startAngle / Math.PI * 180, c = t.layout.endAngle / Math.PI * 180, m = i; ;) {
                    if (r && m > c || !r && c > m)break;
                    var V = m / 180 * Math.PI, U = [Math.cos(V), Math.sin(V)], g = p.scale([], U, h[1] + 1);
                    p.add(g, g, l);
                    var y = p.scale([], U, h[1] + this.scaleLineLength);
                    p.add(y, y, l);
                    var f = new o({
                        zlevel: this.getZlevelBase() - 1,
                        hoverable: !1,
                        style: {
                            xStart: g[0],
                            yStart: g[1],
                            xEnd: y[0],
                            yEnd: y[1],
                            lineCap: "round",
                            brushType: "stroke",
                            strokeColor: "#666",
                            lineWidth: 1
                        }
                    });
                    this.shapeList.push(f), m += d * this.scaleUnitAngle
                }
                if (e.showScaleText)for (var _ = i, b = 5 * u * this.scaleUnitAngle, x = 0; ;) {
                    if (r && _ > c || !r && c > _)break;
                    var V = _;
                    V %= 360, 0 > V && (V += 360);
                    var k = 90 >= V || V >= 270, L = new a({
                        zlevel: this.getZlevelBase() - 1,
                        hoverable: !1,
                        style: {
                            x: k ? h[1] + this.scaleLineLength + 4 : -h[1] - this.scaleLineLength - 4,
                            y: 0,
                            text: Math.round(10 * x) / 10 + n,
                            textAlign: k ? "left" : "right"
                        },
                        position: l.slice(),
                        rotation: k ? [-V / 180 * Math.PI, 0, 0] : [-(V + 180) / 180 * Math.PI, 0, 0]
                    });
                    this.shapeList.push(L), x += b * s, _ += d * this.scaleUnitAngle * 5
                }
            }, this)
        }, refresh: function (e) {
            if (e && (this.option = e, this.series = e.series), this.legend = this.component.legend, this.legend)this.getColor = function (e) {
                return this.legend.getColor(e)
            }, this.isSelected = function (e) {
                return this.legend.isSelected(e)
            }; else {
                var t = {}, i = 0;
                this.getColor = function (e) {
                    return t[e] ? t[e] : (t[e] || (t[e] = this.zr.getColor(i++)), t[e])
                }, this.isSelected = function () {
                    return !0
                }
            }
            this.backupShapeList(), this._init()
        }, reformOption: function (e) {
            var t = m.merge;
            e = t(e || {}, this.ecTheme.chord), e.itemStyle.normal.label.textStyle = t(e.itemStyle.normal.label.textStyle || {}, this.ecTheme.textStyle)
        }
    }, m.inherits(t, n), m.inherits(t, i), e("../chart").define("chord", t), t
}), define("echarts/util/shape/Ribbon", ["require", "zrender/shape/Base", "zrender/shape/util/PathProxy", "zrender/tool/util", "zrender/tool/area"], function (e) {
    function t(e) {
        i.call(this, e), this._pathProxy = new n
    }

    var i = e("zrender/shape/Base"), n = e("zrender/shape/util/PathProxy"), a = e("zrender/tool/util"), o = e("zrender/tool/area");
    return t.prototype = {
        type: "ribbon", buildPath: function (e, t) {
            var i = t.clockWise || !1, n = this._pathProxy;
            n.begin(e);
            var a = t.x, o = t.y, s = t.r, r = t.source0 / 180 * Math.PI, l = t.source1 / 180 * Math.PI, h = t.target0 / 180 * Math.PI, d = t.target1 / 180 * Math.PI, c = a + Math.cos(r) * s, m = o + Math.sin(r) * s, p = a + Math.cos(l) * s, u = o + Math.sin(l) * s, V = a + Math.cos(h) * s, U = o + Math.sin(h) * s, g = a + Math.cos(d) * s, y = o + Math.sin(d) * s;
            n.moveTo(c, m), n.arc(a, o, t.r, r, l, !i), n.bezierCurveTo(.7 * (a - p) + p, .7 * (o - u) + u, .7 * (a - V) + V, .7 * (o - U) + U, V, U), (t.source0 !== t.target0 || t.source1 !== t.target1) && (n.arc(a, o, t.r, h, d, !i), n.bezierCurveTo(.7 * (a - g) + g, .7 * (o - y) + y, .7 * (a - c) + c, .7 * (o - m) + m, c, m))
        }, getRect: function (e) {
            return e.__rect ? e.__rect : (this._pathProxy.isEmpty() || this.buildPath(null, e), this._pathProxy.fastBoundingRect())
        }, isCover: function (e, t) {
            var i = this.getRect(this.style);
            return e >= i.x && e <= i.x + i.width && t >= i.y && t <= i.y + i.height ? o.isInsidePath(this._pathProxy.pathCommands, 0, "fill", e, t) : void 0
        }
    }, a.inherits(t, i), t
}), define("zrender/shape/BezierCurve", ["require", "./Base", "../tool/util"], function (e) {
    "use strict";
    var t = e("./Base"), i = function (e) {
        this.brushTypeOnly = "stroke", this.textPosition = "end", t.call(this, e)
    };
    return i.prototype = {
        type: "bezier-curve", buildPath: function (e, t) {
            e.moveTo(t.xStart, t.yStart), "undefined" != typeof t.cpX2 && "undefined" != typeof t.cpY2 ? e.bezierCurveTo(t.cpX1, t.cpY1, t.cpX2, t.cpY2, t.xEnd, t.yEnd) : e.quadraticCurveTo(t.cpX1, t.cpY1, t.xEnd, t.yEnd)
        }, getRect: function (e) {
            if (e.__rect)return e.__rect;
            var t = Math.min(e.xStart, e.xEnd, e.cpX1), i = Math.min(e.yStart, e.yEnd, e.cpY1), n = Math.max(e.xStart, e.xEnd, e.cpX1), a = Math.max(e.yStart, e.yEnd, e.cpY1), o = e.cpX2, s = e.cpY2;
            "undefined" != typeof o && "undefined" != typeof s && (t = Math.min(t, o), i = Math.min(i, s), n = Math.max(n, o), a = Math.max(a, s));
            var r = e.lineWidth || 1;
            return e.__rect = {x: t - r, y: i - r, width: n - t + r, height: a - i + r}, e.__rect
        }
    }, e("../tool/util").inherits(i, t), i
}), define("echarts/data/Graph", ["require", "zrender/tool/util"], function (e) {
    var t = e("zrender/tool/util"), i = function (e) {
        this._directed = e || !1, this.nodes = [], this.edges = [], this._nodesMap = {}, this._edgesMap = {}
    };
    i.prototype.isDirected = function () {
        return this._directed
    }, i.prototype.addNode = function (e, t) {
        if (this._nodesMap[e])return this._nodesMap[e];
        var n = new i.Node(e, t);
        return this.nodes.push(n), this._nodesMap[e] = n, n
    }, i.prototype.getNodeById = function (e) {
        return this._nodesMap[e]
    }, i.prototype.addEdge = function (e, t, n) {
        if ("string" == typeof e && (e = this._nodesMap[e]), "string" == typeof t && (t = this._nodesMap[t]), e && t) {
            var a = e.id + "-" + t.id;
            if (this._edgesMap[a])return this._edgesMap[a];
            var o = new i.Edge(e, t, n);
            return this._directed && (e.outEdges.push(o), t.inEdges.push(o)), e.edges.push(o), e !== t && t.edges.push(o), this.edges.push(o), this._edgesMap[a] = o, o
        }
    }, i.prototype.removeEdge = function (e) {
        var i = e.node1, n = e.node2, a = i.id + "-" + n.id;
        this._directed && (i.outEdges.splice(t.indexOf(i.outEdges, e), 1), n.inEdges.splice(t.indexOf(n.inEdges, e), 1)), i.edges.splice(t.indexOf(i.edges, e), 1), i !== n && n.edges.splice(t.indexOf(n.edges, e), 1), delete this._edgesMap[a], this.edges.splice(t.indexOf(this.edges, e), 1)
    }, i.prototype.getEdge = function (e, t) {
        return "string" != typeof e && (e = e.id), "string" != typeof t && (t = t.id), this._directed ? this._edgesMap[e + "-" + t] || this._edgesMap[t + "-" + e] : this._edgesMap[e + "-" + t]
    }, i.prototype.removeNode = function (e) {
        if ("string" != typeof e || (e = this._nodesMap[e])) {
            delete this._nodesMap[e.id], this.nodes.splice(t.indexOf(this.nodes, e), 1);
            for (var i = 0; i < this.edges.length;) {
                var n = this.edges[i];
                n.node1 === e || n.node2 === e ? this.removeEdge(n) : i++
            }
        }
    }, i.prototype.filterNode = function (e, t) {
        for (var i = this.nodes.length, n = 0; i > n;)e.call(t, this.nodes[n], n) ? n++ : (this.removeNode(this.nodes[n]), i--)
    }, i.prototype.filterEdge = function (e, t) {
        for (var i = this.edges.length, n = 0; i > n;)e.call(t, this.edges[n], n) ? n++ : (this.removeEdge(this.edges[n]), i--)
    }, i.prototype.eachNode = function (e, t) {
        for (var i = this.nodes.length, n = 0; i > n; n++)this.nodes[n] && e.call(t, this.nodes[n], n)
    }, i.prototype.eachEdge = function (e, t) {
        for (var i = this.edges.length, n = 0; i > n; n++)this.edges[n] && e.call(t, this.edges[n], n)
    }, i.prototype.clear = function () {
        this.nodes.length = 0, this.edges.length = 0, this._nodesMap = {}, this._edgesMap = {}
    }, i.prototype.breadthFirstTraverse = function (e, t, i, n) {
        if ("string" == typeof t && (t = this._nodesMap[t]), t) {
            var a = "edges";
            "out" === i ? a = "outEdges" : "in" === i && (a = "inEdges");
            for (var o = 0; o < this.nodes.length; o++)this.nodes[o].__visited = !1;
            if (!e.call(n, t, null))for (var s = [t]; s.length;)for (var r = s.shift(), l = r[a], o = 0; o < l.length; o++) {
                var h = l[o], d = h.node1 === r ? h.node2 : h.node1;
                if (!d.__visited) {
                    if (e.call(d, d, r))return;
                    s.push(d), d.__visited = !0
                }
            }
        }
    }, i.prototype.clone = function () {
        for (var e = new i(this._directed), t = 0; t < this.nodes.length; t++)e.addNode(this.nodes[t].id, this.nodes[t].data);
        for (var t = 0; t < this.edges.length; t++) {
            var n = this.edges[t];
            e.addEdge(n.node1.id, n.node2.id, n.data)
        }
        return e
    };
    var n = function (e, t) {
        this.id = e, this.data = t || null, this.inEdges = [], this.outEdges = [], this.edges = []
    };
    n.prototype.degree = function () {
        return this.edges.length
    }, n.prototype.inDegree = function () {
        return this.inEdges.length
    }, n.prototype.outDegree = function () {
        return this.outEdges.length
    };
    var a = function (e, t, i) {
        this.node1 = e, this.node2 = t, this.data = i || null
    };
    return i.Node = n, i.Edge = a, i.fromMatrix = function (e, t, n) {
        if (t && t.length && t[0].length === t.length && e.length === t.length) {
            for (var a = t.length, o = new i(n), s = 0; a > s; s++) {
                var r = o.addNode(e[s].id, e[s]);
                r.data.value = 0, n && (r.data.outValue = r.data.inValue = 0)
            }
            for (var s = 0; a > s; s++)for (var l = 0; a > l; l++) {
                var h = t[s][l];
                n && (o.nodes[s].data.outValue += h, o.nodes[l].data.inValue += h), o.nodes[s].data.value += h, o.nodes[l].data.value += h
            }
            for (var s = 0; a > s; s++)for (var l = s; a > l; l++) {
                var h = t[s][l];
                if (0 !== h) {
                    var d = o.nodes[s], c = o.nodes[l], m = o.addEdge(d, c, {});
                    if (m.data.weight = h, s !== l && n && t[l][s]) {
                        var p = o.addEdge(c, d, {});
                        p.data.weight = t[l][s]
                    }
                }
            }
            return o
        }
    }, i
}), define("echarts/layout/Chord", ["require"], function () {
    var e = function (e) {
        e = e || {}, this.sort = e.sort || null, this.sortSub = e.sortSub || null, this.padding = .05, this.startAngle = e.startAngle || 0, this.clockWise = null == e.clockWise ? !1 : e.clockWise, this.center = e.center || [0, 0], this.directed = !0
    };
    e.prototype.run = function (e) {
        e instanceof Array || (e = [e]);
        var n = e.length;
        if (n) {
            for (var a = e[0], o = a.nodes.length, s = [], r = 0, l = 0; o > l; l++) {
                var h = a.nodes[l], d = {size: 0, subGroups: [], node: h};
                s.push(d);
                for (var c = 0, m = 0; m < e.length; m++) {
                    var p = e[m], u = p.getNodeById(h.id);
                    if (u) {
                        d.size += u.layout.size;
                        for (var V = this.directed ? u.outEdges : u.edges, U = 0; U < V.length; U++) {
                            var g = V[U], y = g.layout.weight;
                            d.subGroups.push({weight: y, edge: g, graph: p}), c += y
                        }
                    }
                }
                r += d.size;
                for (var f = d.size / c, U = 0; U < d.subGroups.length; U++)d.subGroups[U].weight *= f;
                "ascending" === this.sortSub ? d.subGroups.sort(t) : "descending" === this.sort && (d.subGroups.sort(t), d.subGroups.reverse())
            }
            "ascending" === this.sort ? s.sort(i) : "descending" === this.sort && (s.sort(i), s.reverse());
            for (var f = (2 * Math.PI - this.padding * o) / r, _ = this.startAngle, b = this.clockWise ? 1 : -1, l = 0; o > l; l++) {
                var d = s[l];
                d.node.layout.startAngle = _, d.node.layout.endAngle = _ + b * d.size * f, d.node.layout.subGroups = [];
                for (var U = 0; U < d.subGroups.length; U++) {
                    var x = d.subGroups[U];
                    x.edge.layout.startAngle = _, _ += b * x.weight * f, x.edge.layout.endAngle = _
                }
                _ = d.node.layout.endAngle + b * this.padding
            }
        }
    };
    var t = function (e, t) {
        return e.weight - t.weight
    }, i = function (e, t) {
        return e.size - t.size
    };
    return e
});