scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/components/shipment/common/screens/ShipmentLineDetailsExtnUI", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!ias/utils/ContextUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!wsc/components/shipment/common/utils/ShipmentUtils", "scbase/loader!wsc/components/shipment/backroompick/utils/BackroomPickUpUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils"],
    function (
        _dojodeclare,
        _extnShipmentLineDetailsExtnUI,
        _scScreenUtils,
        _scModelUtils,
        _iasContextUtils,
        _iasUIUtils,
        _scBaseUtils,
        _wscShipmentUtils,
        _wscBackroomPickUpUtils,
        _scEventUtils,
        _scWidgetUtils
    ) {
        return _dojodeclare("extn.components.shipment.common.screens.ShipmentLineDetailsExtn", [_extnShipmentLineDetailsExtnUI], {
            // custom code here
            callMultiApiShortageUpdate: function (
                args) {
                var shortedShipmentLineModel = null;
                var shipmentLinePickedModel = null;
                shipmentLinePickedModel = _scScreenUtils.getTargetModel(
                    this, "ShipmentLine_Output", null);
                shortedShipmentLineModel = _scBaseUtils.getModelValueFromBean("ShortedShipmentLine", args);
                var updateShortageModel = null;
                updateShortageModel = _wscBackroomPickUpUtils.getShortageChangeShipmentModel(
                    shipmentLinePickedModel, args);
                if (
                    _scBaseUtils.equals("Y", _scModelUtils.getStringValueFromPath("ShipmentLine.MarkAllShortLineWithShortage", shortedShipmentLineModel))) {
                    var shipmentModel = null;
                    shipmentModel = {};
                    shipmentModel = _scModelUtils.createModelObjectFromKey("Shipment", shipmentModel);
                    _scModelUtils.setStringValueAtModelPath("Shipment.Action", "MarkAllLinesShortage", shipmentModel);
                    _scModelUtils.setStringValueAtModelPath("Shipment.ShipmentKey", _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", updateShortageModel), shipmentModel);
                    updateShortageModel = shipmentModel;
                } else {
                    _scModelUtils.setStringValueAtModelPath("Shipment.Action", "MarkLineAsShortage", updateShortageModel);
                }
                _scModelUtils.setStringValueAtModelPath("Shipment.ShortageReasonCode", _scModelUtils.getStringValueFromPath("ShipmentLine.ShortageReasonCode", shortedShipmentLineModel), updateShortageModel);
                _scModelUtils.setStringValueAtModelPath("Shipment.ShortageReasonCodeActual", _scModelUtils.getStringValueFromPath("ShipmentLine.ShortageReasonCodeActual", shortedShipmentLineModel), updateShortageModel);
                var eventArgs = null;
                var eventDefn = null;
                eventDefn = {};
                eventArgs = {};
                _scBaseUtils.setAttributeValue("inputData", updateShortageModel, eventArgs);
                _scBaseUtils.setAttributeValue("argumentList", eventArgs, eventDefn);
                _scEventUtils.fireEventToParent(
                    this, "updateShipmentLineDetails", eventDefn);
            },
            updateItemDetails: function () {
                var shipmentLine = _scScreenUtils.getModel(this, "ShipmentLine");
                _iasUIUtils.callApi(this, {
                    Item: {
                        ItemID: shipmentLine.ShipmentLine.ItemID
                    }
                }, "extn_getItemDetails", null);
                console.log('shipmentLine', shipmentLine);
            },
            updateShipmentLine: function (modelOutput) {
                console.log('modelOutput category', modelOutput);
                var shipmentLine = _scScreenUtils.getModel(this, "ShipmentLine");
                var categoryID = "";
                var categoryDescription = "";
                var LM = shipmentLine.ShipmentLine.OrderLine.ItemDetails.ClassificationCodes.CommodityCode;
                var MCAT = shipmentLine.ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ProductLine;
                if (modelOutput.CategoryList && modelOutput.CategoryList.Category) {
                    categoryID = modelOutput.CategoryList.Category[0].CategoryID || "";
                    categoryDescription = modelOutput.CategoryList.Category[0].ShortDescription || "";
                }
                if (LM === categoryID)
                    shipmentLine.ShipmentLine.OrderLine.ItemDetails.ClassificationCodes.CommodityCode = LM + " - " + categoryDescription;
                if (MCAT === categoryID)
                    shipmentLine.ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ProductLine = MCAT + " - " + categoryDescription;
                console.log('shipmentLine', shipmentLine);
                _scScreenUtils.setModel(this, "ShipmentLine", shipmentLine, null);
            },
            updateDescription: function () {
                var shipmentLine = _scScreenUtils.getModel(this, "ShipmentLine");
                var LM = shipmentLine.ShipmentLine.OrderLine.ItemDetails.ClassificationCodes.CommodityCode;
                var MCAT = shipmentLine.ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ProductLine;
                _iasUIUtils.callApi(this, {
                    Category: {
                        CategoryID: LM
                    }
                }, "extn_updateDescription", null);
                _iasUIUtils.callApi(this, {
                    Category: {
                        CategoryID: MCAT
                    }
                }, "extn_updateDescription", null);
            },
            getBrand: function (dataValue, screen, widget, namespace, modelObj, options) {
                var attributeArray = modelObj.ItemList.Item[0].AdditionalAttributeList.AdditionalAttribute;
                if (attributeArray) {
                    for (var i = 0; i < attributeArray.length; i++) {
                        if (attributeArray[i].Name === "Brand")
                            return attributeArray[i].Value
                    }
                }
                return "";
            },
            getSeries: function (dataValue, screen, widget, namespace, modelObj, options) {
                var attributeArray = modelObj.ItemList.Item[0].AdditionalAttributeList.AdditionalAttribute;
                if (attributeArray) {
                    for (var i = 0; i < attributeArray.length; i++) {
                        if (attributeArray[i].Name === "Series")
                            return attributeArray[i].Value
                    }
                }
                return "";
            },
            getSubject: function (dataValue, screen, widget, namespace, modelObj, options) {
                var attributeArray = modelObj.ItemList.Item[0].AdditionalAttributeList.AdditionalAttribute;
                if (attributeArray) {
                    for (var i = 0; i < attributeArray.length; i++) {
                        if (attributeArray[i].Name === "Subject")
                            return attributeArray[i].Value
                    }
                }
                return "";
            },
            getAuthor: function (dataValue, screen, widget, namespace, modelObj, options) {
                var attributeArray = modelObj.ItemList.Item[0].AdditionalAttributeList.AdditionalAttribute;
                if (attributeArray) {
                    for (var i = 0; i < attributeArray.length; i++) {
                        if (attributeArray[i].Name === "Author")
                            return attributeArray[i].Value
                    }
                }
                return "";
            }
        });
    });