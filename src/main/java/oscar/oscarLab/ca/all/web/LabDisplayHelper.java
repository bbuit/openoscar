package oscar.oscarLab.ca.all.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.caisi_integrator.ws.CachedDemographicLabResult;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.caisi_integrator.ws.FacilityIdLabResultCompositePk;
import org.oscarehr.common.dao.Hl7TextMessageDao;
import org.oscarehr.common.hl7.v2.oscar_to_oscar.DataTypeUtils;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import oscar.oscarLab.ca.all.AcknowledgementData;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.oscarMDS.data.ReportStatus;


public class LabDisplayHelper {
	public static String makeLabKey(Integer demographicId, String segmentId, String labType, String labDateTime) {
		return ("" + demographicId + ':' + segmentId + ':' + labType + ':' + labDateTime);
	}

	public static CachedDemographicLabResult getRemoteLab(Integer remoteFacilityId, String remoteLabKey) throws MalformedURLException {
		DemographicWs demographicWs = CaisiIntegratorManager.getDemographicWs();
		FacilityIdLabResultCompositePk pk = new FacilityIdLabResultCompositePk();
		pk.setIntegratorFacilityId(remoteFacilityId);
		pk.setLabResultId(remoteLabKey);
		CachedDemographicLabResult cachedDemographicLabResult = demographicWs.getCachedDemographicLabResult(pk);

		return (cachedDemographicLabResult);
	}

	public static Document labToXml(LabResultData lab) throws ParserConfigurationException, UnsupportedEncodingException {
		Document doc = XmlUtils.newDocument("LabResult");
		XmlUtils.appendChildToRoot(doc, "acknowledgedStatus", lab.getAcknowledgedStatus());
		XmlUtils.appendChildToRoot(doc, "accessionNumber", lab.accessionNumber);
		XmlUtils.appendChildToRoot(doc, "dateTime", lab.getDateTime());
		XmlUtils.appendChildToRoot(doc, "discipline", lab.getDiscipline());
		XmlUtils.appendChildToRoot(doc, "healthNumber", lab.getHealthNumber());
		XmlUtils.appendChildToRoot(doc, "labPatientId", lab.getLabPatientId());
		XmlUtils.appendChildToRoot(doc, "patientName", lab.getPatientName());
		XmlUtils.appendChildToRoot(doc, "priority", lab.getPriority());
		XmlUtils.appendChildToRoot(doc, "reportStatus", lab.getReportStatus());
		XmlUtils.appendChildToRoot(doc, "requestingClient", lab.getRequestingClient());
		XmlUtils.appendChildToRoot(doc, "segmentID", lab.getSegmentID());
		XmlUtils.appendChildToRoot(doc, "sex", lab.getSex());
		XmlUtils.appendChildToRoot(doc, "ackCount", "" + lab.getAckCount());
		XmlUtils.appendChildToRoot(doc, "multipleAckCount", "" + lab.getMultipleAckCount());
		XmlUtils.appendChildToRoot(doc, "labType", "" + lab.labType);

		ArrayList<ReportStatus> acknowledgments = AcknowledgementData.getAcknowledgements(lab.getSegmentID());
		for (ReportStatus reportStatus : acknowledgments) {
			addAcknowledgment(doc, reportStatus);
		}

		String multiLabId = Hl7textResultsData.getMatchingLabs(lab.getSegmentID());
		XmlUtils.appendChildToRoot(doc, "multiLabId", multiLabId);

		Hl7TextMessageDao hl7TextMessageDao = (Hl7TextMessageDao) SpringUtils.getBean("hl7TextMessageDao");
		Hl7TextMessage hl7TextMessage = hl7TextMessageDao.find(Integer.parseInt(lab.getSegmentID()));
		String type = hl7TextMessage.getType();
		XmlUtils.appendChildToRoot(doc, "hl7TextMessageType", type);
		String hl7Body = DataTypeUtils.decodeBase64StoString(hl7TextMessage.getBase64EncodedeMessage());
		XmlUtils.appendChildToRoot(doc, "hl7TextMessageBody", hl7Body);

		
		
		return (doc);
	}

	private static void addAcknowledgment(Document doc, ReportStatus reportStatus) {
		Node rootNode = doc.getFirstChild();

		Element child = doc.createElement("ReportStatus");
		XmlUtils.appendChild(doc, child, "providerName", reportStatus.getProviderName());
		XmlUtils.appendChild(doc, child, "providerNo", reportStatus.getProviderNo());
		XmlUtils.appendChild(doc, child, "status", reportStatus.getStatus());
		XmlUtils.appendChild(doc, child, "comment", reportStatus.getComment());
		XmlUtils.appendChild(doc, child, "timestamp", reportStatus.getTimestamp());
		XmlUtils.appendChild(doc, child, "segmentId", reportStatus.getID());

		rootNode.appendChild(child);
	}

	public static Document getXmlDocument(CachedDemographicLabResult cachedDemographicLabResult) throws IOException, SAXException, ParserConfigurationException
	{
		return(XmlUtils.toDocument(cachedDemographicLabResult.getData()));		
	}
	
	public static ArrayList<ReportStatus> getReportStatus(Document cachedDemographicLabResultXmlData) {
		ArrayList<ReportStatus> results = new ArrayList<ReportStatus>();

		Node rootNode = cachedDemographicLabResultXmlData.getFirstChild();

		NodeList nodeList = rootNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if ("ReportStatus".equals(node.getNodeName())) {
				results.add(toReportStatus(node));
			}
		}

		return (results);
	}

	private static ReportStatus toReportStatus(Node node) {
		ReportStatus reportStatus=new ReportStatus();
		reportStatus.setComment(XmlUtils.getChildNodeTextContents(node, "comment"));
		reportStatus.setProviderName(XmlUtils.getChildNodeTextContents(node, "providerName"));
		reportStatus.setProviderNo(XmlUtils.getChildNodeTextContents(node, "providerNo"));
		reportStatus.setSegmentID(XmlUtils.getChildNodeTextContents(node, "segmentId"));
		reportStatus.setStatus(XmlUtils.getChildNodeTextContents(node, "status"));
		reportStatus.setTimestamp(XmlUtils.getChildNodeTextContents(node, "timestamp"));
		
		return(reportStatus);
	}
	
	public static String getMultiLabId(Document cachedDemographicLabResultXmlData)
	{
		Node rootNode = cachedDemographicLabResultXmlData.getFirstChild();
		return(XmlUtils.getChildNodeTextContents(rootNode, "multiLabId"));
	}
	
	public static MessageHandler getMessageHandler(Document cachedDemographicLabResultXmlData)
	{
		Node rootNode = cachedDemographicLabResultXmlData.getFirstChild();
		String hl7Type=XmlUtils.getChildNodeTextContents(rootNode, "hl7TextMessageType");
		String hl7Body=XmlUtils.getChildNodeTextContents(rootNode, "hl7TextMessageBody");

		return Factory.getHandler(hl7Type, hl7Body);
	}

	public static String getHl7Body(Document cachedDemographicLabResultXmlData)
	{
		Node rootNode = cachedDemographicLabResultXmlData.getFirstChild();
		String hl7Body=XmlUtils.getChildNodeTextContents(rootNode, "hl7TextMessageBody");

		return hl7Body;
	}
}