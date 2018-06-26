
scDefine(["dojo/text!./templates/AdvancedSearchExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CheckBoxList","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CheckBoxListDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxCheckBoxList
			 ,
			    _scplat
			 ,
			    _scCheckBoxListDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.mobile.home.search.AdvancedSearchExtnUI",
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
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_3'
						,
	  value: 'extn_getStatusList_mashup_output'
						
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

]
}

});
});


