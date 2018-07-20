scDefine(["dojo/text!./templates/ShipmentSummaryShipmentLineDetailsExtn.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/utils/ScreenUtils"], function (
	templateText,
	_dojodeclare,
	_dojokernel,
	_dojolang,
	_dojotext,
	_scplat,
	_scCurrencyDataBinder,
	_scBaseUtils,
	_scDataLabel,
	_scScreenUtils
) {
	return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryShipmentLineDetailsExtnUI", [], {
		templateString: templateText







			,
		namespaces: {
			targetBindingNamespaces: [],
			sourceBindingNamespaces: [{
					scExtensibilityArrayItemId: 'extn_SourceNamespaces_2',
					description: "for the extra details",
					value: 'extn_itemDetails_extraDetails'

				},
				{
					scExtensibilityArrayItemId: 'extn_SourceNamespaces_3',
					description: "for extra details",
					value: 'extn_itemDetails'

				}

			]
		}


		,
		hotKeys: []

			,
		events: []

			,
		subscribers: {

			local: [

				{
					eventId: 'afterScreenInit'

						,
					sequence: '51'




						,
					handler: {
						methodName: "updateItemDetails"


					}
				}

			]
		},
		handleMashupOutput: function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
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
		}
	});



});