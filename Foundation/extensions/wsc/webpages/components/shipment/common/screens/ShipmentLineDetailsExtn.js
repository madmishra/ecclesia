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
            }
        });
    });