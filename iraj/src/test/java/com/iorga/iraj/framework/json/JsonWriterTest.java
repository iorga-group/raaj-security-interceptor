package com.iorga.iraj.framework.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.iorga.iraj.framework.annotation.ContextParam;
import com.iorga.iraj.framework.annotation.ContextParams;
import com.iorga.iraj.framework.annotation.ContextPath;

@SuppressWarnings("unused")
public class JsonWriterTest {

	public static class Simple {
		private String field;
		public Simple(final String field) {
			this.field = field;
		}
		public String getField() {
			return field;
		}
		public void setField(final String field) {
			this.field = field;
		}
	}
	public static class SimpleTemplateWithoutAnnotation {
		private String field;
	}

	@ContextParam(Simple.class)
	public static class SimpleTemplate {
		private String field;
	}

	@Test(expected = IllegalArgumentException.class)
	public void mustThrowExceptionIfContextParamNotFound() throws WebApplicationException, IOException {
		final Simple context = new Simple("test");
		final StreamingOutput output = new JsonWriter().writeWithTemplate(SimpleTemplateWithoutAnnotation.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		baos.toString();
	}

	@Test
	public void jsonWriteSimpleClass() throws WebApplicationException, IOException {
		final Simple context = new Simple("test");
		final StreamingOutput output = new JsonWriter().writeWithTemplate(SimpleTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"field\":\"test\"}", baos.toString());
	}

	public static class SimpleContainer {
		private String field;
		private Simple simple;
		public SimpleContainer(final String field, final Simple simple) {
			this.field = field;
			this.simple = simple;
		}
		public String getField() {
			return field;
		}
		public void setField(final String field) {
			this.field = field;
		}
		public Simple getSimple() {
			return simple;
		}
		public void setSimple(final Simple simple) {
			this.simple = simple;
		}
	}

	@ContextParam(SimpleContainer.class)
	public static class SimpleContainerTemplate {
		private String field;
		private SimpleTemplate simple;
	}
	@Test
	public void jsonWriteSimpleContainter() throws WebApplicationException, IOException {
		final SimpleContainer context = new SimpleContainer("test", new Simple("test2"));
		final StreamingOutput output = new JsonWriter().writeWithTemplate(SimpleContainerTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"field\":\"test\",\"simple\":{\"field\":\"test2\"}}", baos.toString());
	}

	@ContextParam(SimpleContainer.class)
	public static class DeepContextPathTemplate {
		@ContextPath("simple.field")
		private String deepField;
	}
	@Test
	public void jsonWriteDeepContextPath() throws WebApplicationException, IOException {
		final SimpleContainer context = new SimpleContainer("test", new Simple("test2"));
		final StreamingOutput output = new JsonWriter().writeWithTemplate(DeepContextPathTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"deepField\":\"test2\"}", baos.toString());
	}

	@ContextParam(SimpleContainer.class)
	public static class DeepContextPathSameNameTemplate {
		@ContextPath("simple.field")
		private String field;
	}
	@Test
	public void jsonWriteDeepContextPathSameName() throws WebApplicationException, IOException {
		final SimpleContainer context = new SimpleContainer("test", new Simple("test2"));
		final StreamingOutput output = new JsonWriter().writeWithTemplate(DeepContextPathSameNameTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"field\":\"test2\"}", baos.toString());
	}

	@ContextParam(Simple.class)
	public static class MethodTemplate {
		public static String getFieldAppended(final Simple simple) {
			return simple.getField() + "111";
		}
	}
	@Test
	public void jsonWriteMethodTemplate() throws WebApplicationException, IOException {
		final Simple context = new Simple("test");
		final StreamingOutput output = new JsonWriter().writeWithTemplate(MethodTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"fieldAppended\":\"test111\"}", baos.toString());
	}

	@ContextParam(Simple.class)
	public static class NotStaticMethodTemplate {
		public String getFieldAppended(final Simple simple) {
			return simple.getField() + "111";
		}
	}
	@Test(expected = IllegalArgumentException.class)
	public void mustHaveStaticPublicMethod() throws WebApplicationException, IOException {
		final Simple context = new Simple("test");
		final StreamingOutput output = new JsonWriter().writeWithTemplate(NotStaticMethodTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
	}

	public static class SimpleFields {
		private int smallInt;
		private Integer bigInt;
		private double smallDouble;
		private Double bigDouble;
		private float smallFloat;
		private Float bigFloat;
		private short smallShort;
		private Short bigShort;
		private byte smallByte;
		private Byte bigByte;
		private char smallChar;
		private Character bigChar;
		private long smallLong;
		private Long bigLong;
		private boolean smallBoolean;
		private Boolean bigBoolean;
		private BigDecimal bigDecimal;
		private Date date;

		public SimpleFields(final int smallInt, final Integer bigInt, final double smallDouble,
				final Double bigDouble, final float smallFloat, final Float bigFloat,
				final short smallShort, final Short bigShort, final byte smallByte, final Byte bigByte,
				final char smallShar, final Character bigChar, final long smallLong,
				final Long bigLong, final boolean smallBoolean, final Boolean bigBoolean,
				final BigDecimal bigDecimal, final Date date) {
			this.smallInt = smallInt;
			this.bigInt = bigInt;
			this.smallDouble = smallDouble;
			this.bigDouble = bigDouble;
			this.smallFloat = smallFloat;
			this.bigFloat = bigFloat;
			this.smallShort = smallShort;
			this.bigShort = bigShort;
			this.smallByte = smallByte;
			this.bigByte = bigByte;
			this.smallChar = smallShar;
			this.bigChar = bigChar;
			this.smallLong = smallLong;
			this.bigLong = bigLong;
			this.smallBoolean = smallBoolean;
			this.bigBoolean = bigBoolean;
			this.bigDecimal = bigDecimal;
			this.date = date;
		}

		public int getSmallInt() {
			return smallInt;
		}
		public void setSmallInt(final int smallInt) {
			this.smallInt = smallInt;
		}
		public Integer getBigInt() {
			return bigInt;
		}
		public void setBigInt(final Integer bigInt) {
			this.bigInt = bigInt;
		}
		public double getSmallDouble() {
			return smallDouble;
		}
		public void setSmallDouble(final double smallDouble) {
			this.smallDouble = smallDouble;
		}
		public Double getBigDouble() {
			return bigDouble;
		}
		public void setBigDouble(final Double bigDouble) {
			this.bigDouble = bigDouble;
		}
		public float getSmallFloat() {
			return smallFloat;
		}
		public void setSmallFloat(final float smallFloat) {
			this.smallFloat = smallFloat;
		}
		public Float getBigFloat() {
			return bigFloat;
		}
		public void setBigFloat(final Float bigFloat) {
			this.bigFloat = bigFloat;
		}
		public short getSmallShort() {
			return smallShort;
		}
		public void setSmallShort(final short smallShort) {
			this.smallShort = smallShort;
		}
		public Short getBigShort() {
			return bigShort;
		}
		public void setBigShort(final Short bigShort) {
			this.bigShort = bigShort;
		}
		public byte getSmallByte() {
			return smallByte;
		}
		public void setSmallByte(final byte smallByte) {
			this.smallByte = smallByte;
		}
		public Byte getBigByte() {
			return bigByte;
		}
		public void setBigByte(final Byte bigByte) {
			this.bigByte = bigByte;
		}
		public char getSmallChar() {
			return smallChar;
		}
		public void setSmallChar(final char smallShar) {
			this.smallChar = smallShar;
		}
		public Character getBigChar() {
			return bigChar;
		}
		public void setBigChar(final Character bigChar) {
			this.bigChar = bigChar;
		}
		public long getSmallLong() {
			return smallLong;
		}
		public void setSmallLong(final long smallLong) {
			this.smallLong = smallLong;
		}
		public Long getBigLong() {
			return bigLong;
		}
		public void setBigLong(final Long bigLong) {
			this.bigLong = bigLong;
		}
		public boolean isSmallBoolean() {
			return smallBoolean;
		}
		public void setSmallBoolean(final boolean smallBoolean) {
			this.smallBoolean = smallBoolean;
		}
		public Boolean getBigBoolean() {
			return bigBoolean;
		}
		public void setBigBoolean(final Boolean bigBoolean) {
			this.bigBoolean = bigBoolean;
		}
		public BigDecimal getBigDecimal() {
			return bigDecimal;
		}
		public void setBigDecimal(final BigDecimal bigDecimal) {
			this.bigDecimal = bigDecimal;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(final Date date) {
			this.date = date;
		}
	}
	@ContextParam(SimpleFields.class)
	public static class SimpleFieldsTemplate {
		private int smallInt;
		private Integer bigInt;
		private double smallDouble;
		private Double bigDouble;
		private float smallFloat;
		private Float bigFloat;
		private short smallShort;
		private Short bigShort;
		private byte smallByte;
		private Byte bigByte;
		private char smallChar;
		private Character bigChar;
		private long smallLong;
		private Long bigLong;
		private boolean smallBoolean;
		private Boolean bigBoolean;
		private BigDecimal bigDecimal;
		private Date date;
	}
	@Test
	public void testSimpleTypes() throws WebApplicationException, IOException {
		final SimpleFields context = new SimpleFields(1, 2, 3d, 4d, 5f, 6f, (short)7, (short)8, (byte)9, (byte)10, (char)11, (char)12, 13l, 14l, true, false, new BigDecimal(15), new Date(0));
		final StreamingOutput output = new JsonWriter().writeWithTemplate(SimpleFieldsTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"smallInt\":1,\"bigInt\":2,\"smallDouble\":3.0,\"bigDouble\":4.0,\"smallFloat\":5.0,\"bigFloat\":6.0,\"smallShort\":7,\"bigShort\":8,\"smallByte\":9,\"bigByte\":10,\"smallChar\":\"\\u000B\",\"bigChar\":\"\\f\",\"smallLong\":13,\"bigLong\":14,\"smallBoolean\":true,\"bigBoolean\":false,\"bigDecimal\":15,\"date\":0}", baos.toString());
	}

	public static class SimpleList {
		private List<String> strings;

		public SimpleList(final List<String> strings) {
			this.strings = strings;
		}

		public List<String> getStrings() {
			return strings;
		}
		public void setStrings(final List<String> strings) {
			this.strings = strings;
		}
	}
	@ContextParam(SimpleList.class)
	public static class SimpleListTemplate {
		private List<String> strings;
	}
	@Test
	public void testLists() throws WebApplicationException, IOException {
		final SimpleList context = new SimpleList(Lists.newArrayList("toto", "tata"));
		final StreamingOutput output = new JsonWriter().writeWithTemplate(SimpleListTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"strings\":[\"toto\",\"tata\"]}", baos.toString());
	}

	@ContextParams({
		@ContextParam(name = "simple", value = Simple.class),
		@ContextParam(name = "field", value = String.class)
	})
	public static class MapTemplate {
		private SimpleTemplate simple;
		@ContextPath("field")
		private String fieldField;
	}
	@Test
	public void testMultipleContextParams() throws WebApplicationException, IOException {
		final Map<String, Object> context = new HashMap<String, Object>();
		context.put("simple", new Simple("test"));
		context.put("field", "test2");
		final StreamingOutput output = new JsonWriter().writeWithTemplate(MapTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"simple\":{\"field\":\"test\"},\"fieldField\":\"test2\"}", baos.toString());
	}

	public static class DeepList {
		private List<Simple> simples;

		public DeepList(final List<Simple> simples) {
			this.simples = simples;
		}

		public List<Simple> getSimples() {
			return simples;
		}
		public void setSimples(final List<Simple> simples) {
			this.simples = simples;
		}
	}
	@ContextParam(DeepList.class)
	public static class DeepListTemplate {
		private List<SimpleTemplate> simples;
	}
	@Test
	public void testDeepListTemplate() throws WebApplicationException, IOException {
		final DeepList context = new DeepList(Lists.newArrayList(new Simple("test"), new Simple("test2")));
		final StreamingOutput output = new JsonWriter().writeWithTemplate(DeepListTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"simples\":[{\"field\":\"test\"},{\"field\":\"test2\"}]}", baos.toString());
	}


	@ContextParams(
		@ContextParam(name = "simples", value = List.class, parameterizedArguments = DeepList.class)
	)
	public static class DeepListMapTemplate {
		private List<SimpleTemplate> simples;
	}
	@Test
	public void testDeepListMapTemplate() throws WebApplicationException, IOException {
		final Map<String, Object> context = new HashMap<String, Object>();
		context.put("simples", Lists.newArrayList(new Simple("test"), new Simple("test2")));
		final StreamingOutput output = new JsonWriter().writeWithTemplate(DeepListMapTemplate.class, context);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.write(baos);
		Assert.assertEquals("{\"simples\":[{\"field\":\"test\"},{\"field\":\"test2\"}]}", baos.toString());
	}
}