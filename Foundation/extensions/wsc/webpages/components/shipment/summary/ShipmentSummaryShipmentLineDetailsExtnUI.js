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
			if (mashupRefId === "extn_itemDetails") {
				this.updateOriginalObject(modelOutput);
				this.updateDescription();
			}
			if (mashupRefId === "extn_updateDescription") {
				this.updateShipmentLine(modelOutput);
			}
		}
	});
});