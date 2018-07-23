scDefine([
	"dojo/text!./templates/SortPortlet.html",
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!sc/plat/dojo/widgets/Screen",
	"scbase/loader!ias/utils/UIUtils",
	
],
function(
	templateText,
	_dojodeclare,
	
	_scScreen,
	_iasUIUtils
) {
    return _dojodeclare("extn.customScreen.sort.Portlet.SortPortlet", [_scScreen], {
		templateString: templateText,
		uId: "SortPortlet",
		packageName: "extn.customScreen.sort.Portlet",
		className: "SortPortlet",
		 title: "Sort Orders",
     showRelatedTask: true,
		namespaces: {
			targetBindingNamespaces: [{
				description: 'The input to the getOrderList mashup.',
				value: 'getOrderList'
			}],
			sourceBindingNamespaces: [{
				description: 'Initial input to screen',
				value: 'screenInput'
			},{
				description: 'The response from the getOrderList mashup',
				value: 'getOrderList_output'
			}]
		},
		
		events: [],
		subscribers: {
			local: [
			{
				eventId: 'bFindOrder_onClick',
				sequence: '30',
				description: '',
				handler: {
					methodName: "orderSearchAction",
					description: ""
				}
			}
			],
		}
		
	});
});