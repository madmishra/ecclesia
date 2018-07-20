scDefine(["dojo/text!./templates/ShipmentLineDetailsExtn.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/utils/ScreenUtils"], function (
	templateText,
	_dojodeclare,
	_dojokernel,
	_dojolang,
	_dojotext,
	_idxContentPane,
	_scplat,
	_scCurrencyDataBinder,
	_scBaseUtils,
	_scDataLabel,
	_scScreenUtils
) {
	return _dojodeclare("extn.components.shipment.common.screens.ShipmentLineDetailsExtnUI", [], {
		templateString: templateText







			,
		namespaces: {
			targetBindingNamespaces: [],
			sourceBindingNamespaces: [{
					scExtensibilityArrayItemId: 'extn_SourceNamespaces_9',
					value: 'extn_additionalAttributeArray'

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
			if (mashupRefId === "extn_getItemDetails") {
				console.log('modelOutput', modelOutput);
				_scScreenUtils.setModel(this, "extn_additionalAttributeArray", modelOutput, null);
			}
		}


	});
});