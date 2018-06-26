
scDefine(["dojo/text!./templates/ProductDetailsExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel"]
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
			    _scplat
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scDataLabel
){
return _dojodeclare("extn.components.product.details.ProductDetailsExtnUI",
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
	  value: 'extn_detailsModel'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_0'
						,
	  description: "Brand"
						
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
	  eventId: 'tpComponents_onShow'

,	  sequence: '51'




,handler : {
methodName : "updateAuthor"

 
}
}
,
{
	  eventId: 'afterScreenLoad'

,	  sequence: '19'




,handler : {
methodName : "updateAuthor"

 
}
}

]
,
global : [

{
	  eventId: 'afterShowDialog'

,	  sequence: '19'




,handler : {
methodName : "updateAuthor"

 
}
}

]
}

});
});


