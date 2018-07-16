scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/components/batch/batchpick/scanlist/BatchLineDetailsExtnUI", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!ias/utils/UIUtils"],
	function (
		_dojodeclare,
		_extnBatchLineDetailsExtnUI,
		_scScreenUtils,
		_iasUIUtils
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
			}
		});
	});