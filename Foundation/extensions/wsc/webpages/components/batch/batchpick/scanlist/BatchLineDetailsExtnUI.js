scDefine(["dojo/text!./templates/BatchLineDetailsExtn.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel"], function (
	templateText,
	_dojodeclare,
	_dojokernel,
	_dojolang,
	_dojotext,
	_scplat,
	_scCurrencyDataBinder,
	_scBaseUtils,
	_scDataLabel
) {
	return _dojodeclare("extn.components.batch.batchpick.scanlist.BatchLineDetailsExtnUI", [], {
		templateString: templateText







			,
		namespaces: {
			targetBindingNamespaces: [],
			sourceBindingNamespaces: [{
					scExtensibilityArrayItemId: 'extn_SourceNamespaces_1',
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
				console.log('modelOutput',modelOutput); 
				this.updateAllValues(modelOutput);
			}
		}
	});
});