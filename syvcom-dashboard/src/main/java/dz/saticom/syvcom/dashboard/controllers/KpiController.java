package dz.saticom.syvcom.dashboard.controllers;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dz.saticom.syvcom.dashboard.repositories.CustomNativeRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.servlet.http.HttpServletRequest;

import java.awt.*;
import java.awt.image.*;
import java.awt.print.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class KpiController {
	
	 @Autowired
	    private CustomNativeRepository customNativeRepository;
   
	
	
	
	public KpiController() throws ParseException {
	
	}

		 
	@GetMapping("/")
	public String showPage(Model model,@RequestParam(defaultValue="0") int page) throws PrintException, IOException, ParseException{
		System.out.println(customNativeRepository.runNativeQueryOne("SELECT count(*) FROM [AT_BTDVCentrale].[dbo].[T_Liste_Agence]"));    		
		return "dashboard";
	}
	
	@GetMapping("/charts2line")
	@ResponseBody
	public String charts2line(Model model,@RequestParam(defaultValue="0") int dr) throws PrintException, IOException, ParseException{
		//System.out.println(customNativeRepository.runNativeQuery("SELECT count(*) FROM [AT_BTDVCentrale].[dbo].[T_Liste_Agence]"));
		List<Object> nameDrAgence = customNativeRepository.runNativeQueryList(
			    "SELECT upper(D.designation)," + 
			    " A.Id_Agence," + 
			    "  upper(A.Designation) as DesignationAg," + 
			    "  S.Nbr_Cli_Total," + 
			    "  S.Nbr_Cli_Traite_T + S.Nbr_Cli_Traite_P as Nbr_Cli_Traite," + 
			    "  S.Nbr_Cli_Abondon," + 
			    "  S.Nbr_Cli_Attente," + 
			    "  S.LastUpdate, DATEDIFF(hour,DATEADD(ms, DATEDIFF(ms, '00:00:00', S.LastUpdate), CONVERT(DATETIME, S.Date_Prelevement)), GETDATE() ) as heurediff" + 
			    "  from " + 
			    "  T_Liste_DR D" + 
			    "  inner join" + 
			    "  T_Liste_Agence A" + 
			    "  on" + 
			    "   D.Id_DR=" + dr + " and " + 
			    "   D.Id_DR=A.Id_DR and " + 
			    "   A.Fonctionnel=1 " + 
			    "  left join" + 
			    "  Stat_Glob S" + 
			    "   on" + 
			    "   S.Id_Agence=A.Id_Agence and" + 
			    "   S.Date_Prelevement = '2019-08-05'" );
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = mapper.writeValueAsString(nameDrAgence);
		JSONArray array = null;
		try {
			array = new JSONArray(jsonString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(array);
		
		
		//List<Object[]> list2=new List<Object[]>();
		System.out.println(nameDrAgence.size());
		
		JSONObject jo = new JSONObject();
		int Nbr_Cli_TotalDr = 0,Nbr_Cli_TraiteDr = 0,Nbr_Cli_AttenteDr = 0,Nbr_Cli_AbondonDr = 0,Nbr_Agence_Dr_Conn = 0;
		
		for (int i = 0; i < nameDrAgence.size(); i++) 
		{
			  
			 List<?> list = new ArrayList<>();
			    if (nameDrAgence.get(i).getClass().isArray()) {
			        list = Arrays.asList((Object[])nameDrAgence.get(i));
			    } else if (nameDrAgence.get(i) instanceof Collection) {
			        list = new ArrayList<>((Collection<?>)nameDrAgence.get(i));
			    }
			 
			if(i==0)
				jo.put("nameDR", list.get(0));
			if (!(list.get(8) == null) ) /*&& (int)list.get(8)<(int)1*/ 
				Nbr_Agence_Dr_Conn=Nbr_Agence_Dr_Conn + 1;
				
			JSONObject joAg = new JSONObject();
			joAg.put("nameAg", list.get(2));
			joAg.put("idAg", list.get(1));
			joAg.put("Nbr_Cli_Total", list.get(3));
			joAg.put("Nbr_Cli_Traite", list.get(4));
			joAg.put("Nbr_Cli_Attente", list.get(6));
			joAg.put("Nbr_Cli_Abondon", list.get(5));
			
			
			Nbr_Cli_TotalDr += (Short)(list.get(3)==null?(short)0:list.get(3));
			Nbr_Cli_TraiteDr += (Short)(list.get(4)==null?(short)0:list.get(4));
			Nbr_Cli_AttenteDr += (Short)(list.get(6)==null?(short)0:list.get(6));
			Nbr_Cli_AbondonDr += (Short)(list.get(5)==null?(short)0:list.get(5));
			
			
					
			try {
				joAg.put("EF", ((Short)list.get(4)).intValue() * 100 / ((Short)list.get(3)).intValue() );
			} catch (Exception e) {				
				joAg.put("EF", 0 );
			}
			
			List<Object> chart = customNativeRepository.runNativeQueryList(
				    "SELECT DATEDIFF(second,'1970-01-01', [Date_Heure_Prelevement]) " + 
				    "      ,[Nbr_Cli_Total]" + 
				    "      ,[Nbr_Cli_Traite_T] + [Nbr_Cli_Traite_P] as Nbr_Cli_Traite" + 
				    "  FROM [AT_BTDVCentrale].[dbo].[Stat_Avancement] where id_agence=" + list.get(1) + " and CAST([Date_Heure_Prelevement] AS date)='2019-08-05' order by 1" );
			joAg.put("chartdata", chart);
			
			jo.append("agences", joAg);
			/*ArrayList al1 = new ArrayList();			
			al1 = (ArrayList) nameDrAgence.get(i);*/
			//List<Object> list = Arrays.asList(nameDrAgence.get(i));
			//ArrayList<Object> arrList = new ArrayList<Object>(Arrays.asList(nameDrAgence.get(i)));
			//System.out.println(list.get(0).toString());
			//System.out.println(""+arrList.get(0));
			
			//List<Object[]> list2=new Arraylist<Object[]>();
			//list2.addAll(nameDrAgence.get(i));
			
			
			/*
			Object obj2 = (Object)nameDrAgence.get(i);
			ArrayList al1 = new ArrayList();
			al1 = (ArrayList) obj2;
			System.out.println("List2 Value: "+al1);*/
		}
		
		jo.put("Nbr_Cli_TotalDr", Nbr_Cli_TotalDr );
		jo.put("Nbr_Cli_TraiteDr", Nbr_Cli_TraiteDr );
		jo.put("Nbr_Cli_AttenteDr", Nbr_Cli_AttenteDr );
		jo.put("Nbr_Cli_AbondonDr", Nbr_Cli_AbondonDr );
		jo.put("Nbr_Agence_Dr", nameDrAgence.size() );
		jo.put("Nbr_Agence_Dr_Conn", Nbr_Agence_Dr_Conn );
		System.out.println(jo);
		/*for(Object c:array)
			   System.out.println(c[0]);*/
		
		
		List<Object> chart = customNativeRepository.runNativeQueryList(
			    "SELECT DATEDIFF(second,'1970-01-01', [Date_Heure_Prelevement]) " + 
			    "      ,[Nbr_Cli_Total]" + 
			    "      ,[Nbr_Cli_Traite_T] + [Nbr_Cli_Traite_P] as Nbr_Cli_Traite" + 
			    "  FROM [AT_BTDVCentrale].[dbo].[Stat_Avancement] where id_agence=" + dr + " and CAST([Date_Heure_Prelevement] AS date)='2019-08-05' order by 1" );
		
		
		return jo.toString();
	}
	
	
}
