scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/components/shipment/common/screens/ShortageReasonPopupExtnUI", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!ias/utils/UOMUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils"],
	function (
		_dojodeclare,
		_extnShortageReasonPopupExtnUI,
		_iasScreenUtils, _iasUOMUtils, _iasUIUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils,
	) {
		return _dojodeclare("extn.components.shipment.common.screens.ShortageReasonPopupExtn", [_extnShortageReasonPopupExtnUI], {
			// custom code here
			getPopupOutput: function (
				event, bEvent, ctrl, args) {
				var shortageReasonTargetModel = null;
				var shipmentLineModel = null;
				var shortageReasonCode = null;
				shortageReasonTargetModel = {};
				shipmentLineModel = _scScreenUtils.getModel(
					this, "ShipmentLine");
				var shortageReasonModel = null;
				shortageReasonModel = _scBaseUtils.getTargetModel(
					this, "getShortageReasonOutput", null);
				console.log('shortageReasonModel.ShortageReason', shortageReasonModel.ShortageReason);
				var shortedShipmentLineModel = null;
				shortedShipmentLineModel = {};
				shortedShipmentLineModel = _scModelUtils.createModelObjectFromKey(this.entity, shortedShipmentLineModel);
				_scModelUtils.setStringValueAtModelPath(
					this.shortageReasonPath, _scModelUtils.getStringValueFromPath("ShortageReason", shortageReasonModel), shortedShipmentLineModel);
				if (!_scBaseUtils.equals("StoreBatchLine", this.entity)) {
					_scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentLineKey", _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", shipmentLineModel), shortedShipmentLineModel);
					_scModelUtils.setStringValueAtModelPath(
						this.markAllBindingPath, _scModelUtils.getStringValueFromPath("MarkAllShortLineWithShortage", shortageReasonModel), shortedShipmentLineModel);
				}
				if (
					_scBaseUtils.equals(
						this.flowName, "CustomerPick")) {
					var cancelReasonModel = null;
					cancelReasonModel = _scBaseUtils.getTargetModel(
						this, "getCancellationReasonCodeOutput", null);
					_scModelUtils.setStringValueAtModelPath(
						this.cancelReasonPath, _scModelUtils.getStringValueFromPath("CancelReasonCode", cancelReasonModel), shortedShipmentLineModel);
				}
				if (shortedShipmentLineModel.StoreBatchLine)
					shortedShipmentLineModel.StoreBatchLine.ShortageResolutionReason = "AllInventoryShortage";
				else
					shortedShipmentLineModel.ShipmentLine.ShortageReasonCode = "AllInventoryShortage";
			//	shortedShipmentLineModel.ShipmentLine.ShortageReasonCodeActual = _scModelUtils.getStringValueFromPath("ShortageReason", shortageReasonModel);

				return shortedShipmentLineModel;
			},
		});
	});