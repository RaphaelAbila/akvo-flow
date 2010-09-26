package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class QuestionAnswerStore extends BaseDomain {
	private static final long serialVersionUID = 3726562582080475960L;

	@Persistent
	private Long arbitratyNumber;
	@Persistent
	private String questionID;
	@Persistent
	private String type;
	@Persistent
	private String value;
	private Date collectionDate;
	private Long surveyId;
	private Long surveyInstanceId;
	private String scoredValue;

	public String getScoredValue() {
		return scoredValue;
	}

	public void setScoredValue(String scoredValue) {
		this.scoredValue = scoredValue;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyInstanceId() {
		return surveyInstanceId;
	}

	public void setSurveyInstanceId(Long surveyInstanceId) {
		this.surveyInstanceId = surveyInstanceId;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	@Persistent
	private SurveyInstance surveyInstance;

	public SurveyInstance getSurveyInstance() {
		return surveyInstance;
	}

	public void setSurveyInstance(SurveyInstance surveyInstance) {
		this.surveyInstance = surveyInstance;
	}

	public Long getArbitratyNumber() {
		return arbitratyNumber;
	}

	public void setArbitratyNumber(Long arbitratyNumber) {
		this.arbitratyNumber = arbitratyNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getQuestionID() {
		return questionID;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}
}
