scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!dojo/dom", "scbase/loader!extn/components/shipment/summary/ShipmentSummaryShipmentLineDetailsExtnUI"],
	function (
		_dojodeclare, _iasUIUtils, _scScreenUtils, _scWidgetUtils, _dojodom,
		_extnShipmentSummaryShipmentLineDetailsExtnUI
	) {
		return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryShipmentLineDetailsExtn", [_extnShipmentSummaryShipmentLineDetailsExtnUI], {
			// custom code here
			updateItemDetails: function () {
				parentObj = _scScreenUtils.getModel(this, "shipmentLine_Src");
				console.log('parentObj', parentObj);
				var itemKey = parentObj.ShipmentLine.OrderLine.ItemDetails.ItemKey;
				var itemModel = {
					Item: {
						ItemKey: itemKey
					}
				}
				_iasUIUtils.callApi(this, itemModel, "extn_itemDetails", null);
			},
			updateDescription: function () {
				var shipmentLine = _scScreenUtils.getModel(this, "extn_itemDetails");
				var LM = shipmentLine.ClassificationCodes.CommodityCode;
				var MCAT = shipmentLine.PrimaryInformation.ProductLine;
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
			updateShipmentLine: function (modelOutput) {
				console.log('modelOutput category', modelOutput);
				var shipmentLine = _scScreenUtils.getModel(this, "extn_itemDetails");
				var categoryID = "";
				var categoryDescription = "";
				var LM = shipmentLine.ClassificationCodes.CommodityCode;
				var MCAT = shipmentLine.PrimaryInformation.ProductLine;
				if (modelOutput.CategoryList && modelOutput.CategoryList.Category) {
					categoryID = modelOutput.CategoryList.Category[0].CategoryID || "";
					categoryDescription = modelOutput.CategoryList.Category[0].ShortDescription || "";
				}
				if (LM === categoryID)
					shipmentLine.ClassificationCodes.CommodityCode = LM + " - " + categoryDescription;
				if (MCAT === categoryID)
					shipmentLine.PrimaryInformation.ProductLine = MCAT + " - " + categoryDescription;
				console.log('shipmentLine', shipmentLine);
				_scScreenUtils.setModel(this, "extn_itemDetails", shipmentLine, null);
			},
			updateOriginalObject: function (modelOutput) {
				var itemModel = {};
				var extraDetailsArray = [];
				var extraDetailsModel = {};
				if (modelOutput && modelOutput.ItemList && modelOutput.ItemList.Item[0])
					itemModel = modelOutput.ItemList.Item[0];
				if (itemModel && itemModel.AdditionalAttributeList && itemModel.AdditionalAttributeList.AdditionalAttribute)
					extraDetailsArray = itemModel.AdditionalAttributeList.AdditionalAttribute;
				for (var i = 0; i < extraDetailsArray.length; i++) {
					if (extraDetailsArray[i].Name === "Brand")
						extraDetailsModel.Brand = extraDetailsArray[i].Value;
					if (extraDetailsArray[i].Name === "Series")
						extraDetailsModel.Series = extraDetailsArray[i].Value;
					if (extraDetailsArray[i].Name === "Author")
						extraDetailsModel.Author = extraDetailsArray[i].Value;
					if (extraDetailsArray[i].Name === "Subject")
						extraDetailsModel.Subject = extraDetailsArray[i].Value;
				}
				_scScreenUtils.setModel(this, "extn_itemDetails", itemModel, null);
				_scScreenUtils.setModel(this, "extn_itemDetails_extraDetails", extraDetailsModel, null);
			},
			checkIfValExists: function (event, bEvent, ctrl, args) {
				var self = this;
				if (event) {
					_scWidgetUtils.showWidget(self, ctrl, true, null);
					return event;
				}
			}
		});
	});