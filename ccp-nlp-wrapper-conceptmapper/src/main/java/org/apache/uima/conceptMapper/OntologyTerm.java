

/* First created by JCasGen Thu Aug 28 12:00:16 MDT 2008 */
package org.apache.uima.conceptMapper;

import org.apache.uima.conceptMapper.DictTerm;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Thu Aug 28 12:09:59 MDT 2008
 * XML source: /home/williamb/eclipse/NLP-CODE-BASE/desc/ae/util/converter/ConceptMapper2CCPTypeSystemConverter_AE.xml
 * @generated */
public class OntologyTerm extends DictTerm {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(OntologyTerm.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected OntologyTerm() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public OntologyTerm(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public OntologyTerm(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public OntologyTerm(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: ID

  /** getter for ID - gets A field to store the ontology term ID.
   * @generated */
  public String getID() {
    if (OntologyTerm_Type.featOkTst && ((OntologyTerm_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "org.apache.uima.conceptMapper.OntologyTerm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OntologyTerm_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets A field to store the ontology term ID. 
   * @generated */
  public void setID(String v) {
    if (OntologyTerm_Type.featOkTst && ((OntologyTerm_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "org.apache.uima.conceptMapper.OntologyTerm");
    jcasType.ll_cas.ll_setStringValue(addr, ((OntologyTerm_Type)jcasType).casFeatCode_ID, v);}    
  }

    