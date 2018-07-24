scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/components/batch/batchpick/scanlist/BatchLineDetailsExtnUI", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!sc/plat/dojo/utils/EventUtils",
		"scbase/loader!sc/plat/dojo/utils/ModelUtils",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils"
	],
	function (
		_dojodeclare,
		_extnBatchLineDetailsExtnUI,
		_scScreenUtils,
		_iasUIUtils,
		_scBaseUtils, _scEventUtils,
		_scModelUtils, _scScreenUtils, _scWidgetUtils, _scScreenUtils
	) {
		return _dojodeclare("extn.components.batch.batchpick.scanlist.BatchLineDetailsExtn", [_extnBatchLineDetailsExtnUI], {
			// custom code here
			updateItemDetails: function () {
				var shipmentLine = _scScreenUtils.getModel(this, "BatchLine");
				_iasUIUtils.callApi(this, {
					Item: {
						ItemID: shipmentLine.StoreBatchLine.ItemID
					}
				}, "extn_getItemDetails", null);
				console.log('shipmentLine', shipmentLine);
			},
			showHideLabel: function (dataValue, screen, widget,
				namespace, modelObj, options) {
				_scWidgetUtils.hideWidget(this, widget, true);
				return dataValue;
			},
			updateAllValues: function (modelObject) {
				var arrayListObject = modelObject.ItemList.Item[0];
				var MCAT = "";
				var LM = ""
				var arrayList = arrayListObject.AdditionalAttributeList.AdditionalAttribute;
				if (arrayListObject.ClassificationCodes && arrayListObject.ClassificationCodes.CommodityCode)
					MCAT = arrayListObject.ClassificationCodes.CommodityCode;
				if (arrayListObject.PrimaryInformation && arrayListObject.PrimaryInformation.ProductLine)
					LM = arrayListObject.ClassificationCodes.CommodityCode;
				var additionalDetailsObject = {
					MCAT: MCAT,
					LM: LM
				};
				for (var i = 0; i < arrayList.length; i++) {
					if (arrayList[i].Name === "Brand") {
						additionalDetailsObject.Brand = arrayList[i].Value;
					}
					if (arrayList[i].Name === "Series") {
						additionalDetailsObject.Series = arrayList[i].Value;
					}
					if (arrayList[i].Name === "Subject") {
						additionalDetailsObject.Subject = arrayList[i].Value;
					}
					if (arrayList[i].Name === "Author") {
						additionalDetailsObject.Author = arrayList[i].Value;
					}
				}
				_scScreenUtils.setModel(this, "extn_additionalAttributeArray", additionalDetailsObject, null);
				console.log('additionalDetailsObject', additionalDetailsObject);
			},
			onShortageReasonSelection: function (actionPerformed,
				model, popupParams) {
				if (!(_scBaseUtils.equals(actionPerformed, "CLOSE"))) {
					var shortedShipmentLineModel = _scScreenUtils
						.getModel(this, "ShortedBatchLineModel");
					console.log("shortedShipmentLineModel : ",
						shortedShipmentLineModel);
					var shortedBatchLineModel = _scScreenUtils
						.getTargetModel(this,
							"BatchLineForRecordShortage",
							null);
					_scModelUtils
						.setStringValueAtModelPath(
							"StoreBatch.Item.ShortageReason",
							_scModelUtils
							.getStringValueFromPath(
								"StoreBatchLine.ShortageResolutionReason",
								shortedShipmentLineModel),
							shortedBatchLineModel);
					_scModelUtils
						.setStringValueAtModelPath(
							"StoreBatch.Item.ShortageReasonActual",
							_scModelUtils
							.getStringValueFromPath(
								"StoreBatchLine.ShortageResolutionReasonActual",
								shortedShipmentLineModel),
							shortedBatchLineModel);

					var eventBean = {};
					_scBaseUtils.setAttributeValue("argumentList", {}, eventBean);
					_scBaseUtils.setAttributeValue(
						"argumentList.BatchLineShortedModel",
						shortedBatchLineModel, eventBean);
					console.log('eventBean', eventBean);
					_scEventUtils
						.fireEventToParent(this,
							"updateShortageForBatchLine",
							eventBean);
				}
			}
		});
	});