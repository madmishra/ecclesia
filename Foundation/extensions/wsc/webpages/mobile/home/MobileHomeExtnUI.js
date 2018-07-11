
scDefine(["dojo/text!./templates/MobileHomeExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Label","scbase/loader!sc/plat/dojo/widgets/Link"]
 , function(			 
			    templateText
			 ,
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojolang
			 ,
			    _dojotext
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLabel
			 ,
			    _scLink
){
return _dojodeclare("extn.mobile.home.MobileHomeExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
		],
		sourceBindingNamespaces :
		[
			{
	  value: 'extn_getShipmentListCount_gw'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_7'
						,
	  description: "Number of gift wrap orders"
						
			}
			
		]
	}

	
	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {

local : [

{
	  eventId: 'afterScreenInit'

,	  sequence: '51'




,handler : {
methodName : "test"

 
}
}
,
{
	  eventId: 'extn_contentpane_gift_onClick'

,	  sequence: '51'




,handler : {
methodName : "openReadyForGiftWrap"

 
}
}
,
{
	  eventId: 'extn_sortpane_onClick'

,	  sequence: '51'




,handler : {
methodName : "movetoSort"

 
}
}

]
}

});
});


