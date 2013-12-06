function IceLayer() {
    this.init = function() {
        var that = this;

        var iceContext = {
            transparency : function() {
                return that.active ? 0.5 : 0.25;
            }
        }

        this.layers.ice = new OpenLayers.Layer.Vector("Ice", {
            styleMap : new OpenLayers.StyleMap({
                "default" : new OpenLayers.Style({
                    fillColor : "${fillColor}",
                    fillOpacity : "${transparency}",
                    strokeWidth : "1",
                    strokeColor : "#000000",
                    strokeOpacity : "0.2",
                    fontColor : "#000000",
                    fontSize : "12px",
                    fontFamily : "Courier New, monospace",
                    label : "${description}",
                    fontOpacity : "${transparency}",
                    fontWeight : "bold"
                }, {
                    context : iceContext
                }),
                "temporary" : new OpenLayers.Style({
                    fillColor : "${fillColor}",
                    fillOpacity : "${transparency}",
                    strokeWidth : "1",
                    strokeColor : "#000000",
                    strokeOpacity : "0.7",
                }, {
                    context : iceContext
                }),
                "select" : new OpenLayers.Style({
                    fillColor : "${fillColor}",
                    fillOpacity : "${transparency}",
                    strokeWidth : "1",
                    strokeColor : "#000",
                    strokeOpacity : "1",
                }, {
                    context : iceContext
                })
            })
        });

        this.selectableLayer = this.layers.ice;
        this.selectableAttribute = "iceDescription";
    }

    this.draw = function(shapes) {
        function colorByDescription(description) {

            if (description.CT == 92)
                return "#979797";
            if (description.CT == 79 || description.CT > 80)
                return "#ff0000";
            if (description.CT == 57 || description.CT > 60)
                return "#ff7c06";
            if (description.CT == 24 || description.CT > 30)
                return "#ffff00";
            if (description.CT > 10)
                return "#8effa0";
            return "#96C7FF";
        }

        this.layers.ice.removeAllFeatures();

        var waterCount = 0;

        for ( var l in shapes) {
            var shape = shapes[l];
            var ice = shape.fragments;
            var features = [];

            for ( var i in ice) {
                var rings = [];
                var polygons = ice[i].polygons;

                for ( var k in polygons) {
                    var polygon = polygons[k];
                    var points = [];
                    for ( var j in polygon) {
                        var p = polygon[j];
                        points.push(embryo.map.createPoint(p.x, p.y));
                    }
                    rings.push(new OpenLayers.Geometry.LinearRing(points));
                }

                var feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon(rings), {
                    fillOpacity : function() {
                        return 0.4 * groupOpacity;
                    },
                    fillColor : colorByDescription(ice[i].description),
                    iceDescription : ice[i].description,
                    description : ""
                });
                feature.attributes.iceDescription = $.extend(ice[i].description, {
                    source : shape.description.id
                });
                if (ice[i].description.POLY_TYPE == 'I') {
                    feature.attributes.description = "";
                } else if (ice[i].description.POLY_TYPE == 'W') {
                    feature.attributes.description = waterCount == 0 ? shape.description.id : "";
                    // modify description to make sure we show it is open water
                    feature.attributes.iceDescription.CT = "1";
                    waterCount++;
                }
                features.push(feature);
            }

            this.layers.ice.addFeatures(features);
        }
    }
}

IceLayer.prototype = new EmbryoLayer();
