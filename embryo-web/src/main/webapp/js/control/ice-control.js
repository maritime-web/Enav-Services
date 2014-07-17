$(function() {

    var module = angular.module('embryo.ice.control', [ 'ui.bootstrap.accordion', 'embryo.control',
            'embryo.ice.service', 'embryo.shape' ]);

    var iceLayer = new IceLayer();
    addLayerToMap("ice", iceLayer, embryo.map);

    var chartsDisplayed = {};

    function displayChartList(divId, chartType, data) {
        data.sort(function(a, b) {
            return b.date - a.date;
        });

        var regionNames = [];
        for ( var i in data) {
            if (regionNames.indexOf(data[i].region) < 0)
                regionNames.push(data[i].region);
        }

        var sortFunction = function(reg1, reg2) {
            if (reg1.indexOf("All Arctic") >= 0 && reg2.indexOf("All Arctic") < 0) {
                return 1;
            } else if (reg1.indexOf("All Arctic") < 0 && reg2.indexOf("All Arctic") >= 0) {
                return -1;
            } else if (reg1.indexOf("Overview") >= 0 && reg2.indexOf("Overview") < 0) {
                return 1;
            } else if (reg1.indexOf("Overview") < 0 && reg2.indexOf("Overview") >= 0) {
                return -1;
            }
            return reg1.localeCompare(reg2);
        };

        regionNames.sort(sortFunction);

        var regions = [];
        for ( var j in regionNames) {
            var regionName = regionNames[j];
            regions.push(regionName);
            for ( var i in data) {
                if (data[i].region == regionName) {
                    regions.push({
                        type : chartType,
                        source : data[i].source,
                        ts : formatTime(data[i].date),
                        size : formatSize(data[i].size),
                        shape : data[i].shapeFileName,
                        date : data[i].date,
                        region : data[i].region
                    });
                }
            }
        }
        return regions;
    }

    function iceController($scope, IceService, $timeout, ShapeService) {
        $scope.selected = {};

        $scope.selectedProvider = {
            key : null
        };

        $scope.changeProvider = function() {
            IceService.setSelectedProvider($scope.selectedProvider);
            IceService.update();
            $("#iceControlPanel .collapse").data("collapse", null);
            openCollapse("#iceControlPanel #icpIceMaps");
        }

        var subscriptionConfig = {
            name : "ice-control",
            providers : function(error, providers) {
                $scope.providers = providers;
                $scope.selectedProvider = IceService.getSelectedProvider("");
            },
            iceCharts : function(error, iceCharts) {
                if (!error) {
                    $scope.iceCharts = displayChartList("#icpIceMaps", "iceChart", iceCharts);
                }
            },
            icebergs : function(error, icebergs) {
                if (!error) {
                    $scope.icebergs = displayChartList("#icpIcebergs", "iceberg", icebergs);
                }
            }
        }

        IceService.subscribe(subscriptionConfig);

        iceLayer.select(function(ice) {
            var lon = null, lat = null;
            if (ice && ice.type == 'iceberg') {
                lon = ice.Long;
                lat = ice.Lat;
            }
            iceLayer.selectIceberg(lon, lat);
            if (ice != null) {
                $scope.selected.open = true
                $scope.selected.observation = ice;
                showIceInformation(ice);
            } else {
                $scope.selected.open = false;
                $scope.selected.observation = null;
            }
            
            if (!$scope.$$phase) {
                $scope.$apply(function() {
                });
            }
        });

        $scope.isDownloaded = function(chart) {
            return chartsDisplayed[chart.type] == chart.shape;
        }

        $scope.download = function($event, chart, charts) {
            $event.preventDefault();
            requestShapefile(chart, function(){
                chartsDisplayed[chart.type] = chart.shape;
                if (!$scope.$$phase) {
                    $scope.$apply(function() {
                    });
                }
            });
        }

        $scope.hideIce = function($event, chart) {
            $event.preventDefault();
            delete chartsDisplayed[chart.type];
            iceLayer.clear(chart.type);
        }

        $scope.zoom = function($event, chart) {
            $event.preventDefault();
            embryo.map.zoomToExtent(iceLayer.layers);
        }

        function requestShapefile(chart, onSuccess) {
            var name = chart.shape;
            var messageId = embryo.messagePanel.show({
                text : "Requesting " + chart.name + " data ..."
            });
            ShapeService.shape(name, {
                parts : name.indexOf("aari.aari_arc") >= 0 ? 2 : 0
            }, function(data) {
                messageId = embryo.messagePanel.replace(messageId, {
                    text : "Drawing " + name,
                });

                function finishedDrawing() {
                    var totalPolygons = 0;
                    var totalPoints = 0;
                    if (chart.type == 'iceberg') {
                        totalPoints = data.fragments.length;
                    } else {
                        for ( var i in data.fragments) {
                            totalPolygons += data.fragments[i].polygons.length;
                            for ( var j in data.fragments[i].polygons)
                                totalPoints += data.fragments[i].polygons[j].length;
                        }
                    }
                    embryo.messagePanel.replace(messageId, {
                        text : totalPolygons + " polygons. " + totalPoints + " points drawn.",
                        type : "success"
                    });

                    onSuccess ? onSuccess() : null;
                }
                data.information = {
                    region : chart.region,
                    date : chart.date
                };
                // Draw shapefile a bit later, just let
                // the browser update the
                // view and show above message
                $timeout(function() {
                    iceLayer.draw(chart.type, [ data ], finishedDrawing);
                }, 10);
            }, function(errorMsg, status) {
                if (status == 410) {
                    errorMsg = errorMsg + " Refreshing ice chart list ... ";
                }
                embryo.messagePanel.replace(messageId, {
                    text : errorMsg,
                    type : "error"
                });

                IceService.update(chart.type + "s");
            });
        }

    }

    module.controller("IceController", [ '$scope', 'IceService', '$timeout', 'ShapeService', iceController ]);

    function createTableHeaderRow(headline) {
        return '<tr><th colspan="2" style="background-color:#eee;">' + headline + '</th></tr>';
    }

    function createTableRow(props) {
        var result = '';
        $.each(props, function(k, v) {
            result += "<tr><th>" + k + "</th><td>" + v + "</td></tr>";
        });
        return result;
    }

    function createIceTable(d) {
        function c(v) {
            switch (v) {
            case "00":
                return "Ice Free";
            case "1":
                return "Open Water";
            case "2":
                return "Bergy Water";
            case "10":
                return "1/10";
            case "12":
                return "1/10 to 2/10";
            case "13":
                return "1/10 to 3/10";
            case "20":
                return "2/10";
            case "23":
                return "2/10 to 3/10";
            case "24":
                return "2/10 to 4/10";
            case "30":
                return "3/10";
            case "34":
                return "3/10 to 4/10";
            case "35":
                return "3/10 to 5/10";
            case "40":
                return "4/10";
            case "45":
                return "4/10 to 5/10";
            case "46":
                return "4/10 to 6/10";
            case "50":
                return "5/10";
            case "56":
                return "5/10 to 6/10";
            case "57":
                return "5/10 to 7/10";
            case "60":
                return "6/10";
            case "67":
                return "6/10 to 7/10";
            case "68":
                return "6/10 to 8/10";
            case "70":
                return "7/10";
            case "78":
                return "7/10 to 8/10";
            case "79":
                return "7/10 to 9/10";
            case "80":
                return "8/10";
            case "81":
                return "8/10 to 10/10";
            case "89":
                return "8/10 to 9/10";
            case "90":
                return "9/10";
            case "91":
                return "9/10 to 10/10, 9+/10";
            case "92":
                return "10/10";
                // case "99": return "Unknown/Undetermined";
                // case "-9": return "Null Value";
            default:
                return "n/a";
            }
        }

        function s(v) {
            switch (v) {
            case "00":
                return "Ice Free";
            case "80":
                return "No stage of development";
            case "81":
                return "New Ice (<10 cm)";
            case "82":
                return "Nilas Ice Rind (<10 cm)";
            case "83":
                return "Young Ice (10 to 30 cm)";
            case "84":
                return "Grey Ice (10 to 15 cm)";
            case "85":
                return "Grey – White Ice (15 to 30 cm)";
            case "86":
                return "First Year Ice (>30 cm) or Brash Ice";
            case "87":
                return "Thin First Year Ice (30 to 70 cm)";
            case "88":
                return "Thin First Year Ice (stage 1)";
            case "89":
                return "Thin First Year Ice (stage 2)";
            case "90":
                return "Code not currently assigned";
            case "91":
                return "Medium First Year Ice (70 to 120 cm)";
            case "92":
                return "Code not currently assigned";
            case "93":
                return "Thick First Year Ice (>120 cm)";
            case "94":
                return "Code not currently assigned";
            case "95":
                return "Old Ice";
            case "96":
                return "Second Year Ice";
            case "97":
                return "Multi-Year Ice";
            case "98":
                return "Glacier Ice (Icebergs)";
                // case "99": return "Unknown/Undetermined";
                // case "-9": return "";
            default:
                return "n/a";
            }
        }

        function f(v) {
            switch (parseFloat(v)) {
            case 11:
                return "Strips and Patches (1/10)";
            case 12:
                return "Strips and Patches (2/10)";
            case 13:
                return "Strips and Patches (3/10)";
            case 14:
                return "Strips and Patches (4/10)";
            case 15:
                return "Strips and Patches (5/10)";
            case 16:
                return "Strips and Patches (6/10)";
            case 17:
                return "Strips and Patches (7/10)";
            case 18:
                return "Strips and Patches (8/10)";
            case 19:
                return "Strips and Patches (9/10)";
            case 20:
                return "Strips and Patches (10/10)";
            case 0:
                return "Pancake Ice";
            case 1:
                return "Shuga/Small Ice Cake, Brash Ice";
            case 2:
                return "Ice Cake";
            case 3:
                return "Small Floe";
            case 4:
                return "Medium Floe";
            case 5:
                return "Big Floe";
            case 6:
                return "Vast Floe";
            case 7:
                return "Giant Floe";
            case 8:
                return "Fast Ice";
            case 9:
                return "Growlers, Floebergs, Floebits";
            case 10:
                return "Icebergs";
            default:
                return "n/a";
            }
            return v;
        }

        var html = "";

        html += createTableHeaderRow('Total');

        html += createTableRow({
            "Concentration" : c(d.CT),
            "Stage of Development (S0)" : s(d.CN),
            "Stage of Development (Sd)" : s(d.CD),
            "Form of Ice" : f(d.CF)
        });

        html += createTableHeaderRow('Thickest Partial');

        html += createTableRow({
            "Concentration" : c(d.CA),
            "Stage of Development" : s(d.SA),
            "Form of Ice" : f(d.FA)
        });

        html += createTableHeaderRow('Second Thickest Partial');

        html += createTableRow({
            "Concentration" : c(d.CB),
            "Stage of Development" : s(d.SB),
            "Form of Ice" : f(d.FB)
        });

        html += createTableHeaderRow('Third Thickest Partial');

        html += createTableRow({
            "Concentration" : c(d.CC),
            "Stage of Development" : s(d.SC),
            "Form of Ice" : f(d.FC)
        });

        return html;
    }

    function createIcebergTable(desc) {
        function getIcebergSize(size) {
            switch (size) {
            case 'S':
                return 'Small';
            case 'M':
                return 'Medium';
            case 'L':
                return 'Large';
            case 'VL':
                return 'Very large';
            default:
                return 'Uncategorized';
            }
        }

        var html = '';

        html += createTableHeaderRow('Iceberg');

        html += createTableRow({
            'Area (m2)' : desc.Area_m2,
            'Longest diameter (m)' : desc.Adj_Size_m,
            'Size category' : getIcebergSize(desc.Size_Catg)
        });

        return html;
    }

    function showIceInformation(iceDescription) {
        $("a[href=#icpSelectedIce]").html("Selected Ice Observation");
        if (iceDescription.type == 'iceberg') {
            $("#icpSelectedIce table").html(createIcebergTable(iceDescription));
        } else {
            $("#icpSelectedIce table").html(createIceTable($.extend(iceDescription, {
                size : 160
            })));
        }

        var source = "Region: " + iceDescription.information.region + "<br/>";
        source += ("Created: " + formatTime(iceDescription.information.date) + " UTC");
        if (iceDescription.type == 'iceberg') {
            source += ("<br/>Position: " + formatLatitude(iceDescription.Lat) + ', ' + formatLongitude(iceDescription.Long));
        }
        $("#icpSelectedIce p").html(source);
    }
});
