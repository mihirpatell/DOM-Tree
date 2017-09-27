package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {		
		String s = sc.nextLine();
		Stack<TagNode> tagStack = new Stack<TagNode>();

		root = new TagNode(s.substring(1, s.length()-1), null, null);
		
		
		tagStack.push(root);
		
		String current = "";
		
		while (sc.hasNextLine()){
			current = sc.nextLine();	
			if (current.charAt(0) == '<'){
				if (current.charAt(1) == '/'){ 
					tagStack.pop();
				}
				else{ 
					TagNode temp = new TagNode(current.substring(1, current.length()-1), null, null);
					if (tagStack.peek().firstChild == null){
						
						tagStack.peek().firstChild = temp;		
						tagStack.push(temp);
						
					}
					else {
						TagNode ptr = tagStack.peek().firstChild;
						while (ptr.sibling != null){
							ptr = ptr.sibling;
						}
						ptr.sibling = temp;
						tagStack.push(temp);
					}
				}
			}
			else {
				TagNode temp = new TagNode(current, null, null);
				if (tagStack.peek().firstChild == null){
					tagStack.peek().firstChild = temp;
				}
				else {
					TagNode ptr = tagStack.peek().firstChild;
					while (ptr.sibling != null){
						ptr = ptr.sibling;
					}
					ptr.sibling = temp;
				}
			}
		}	
	}
	// works
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		recReplaceTag(root, oldTag, newTag);
	}
	private void recReplaceTag (TagNode ptr, String oldTag, String newTag){
		if (ptr.tag.equals(oldTag)){
			ptr.tag=newTag;
		// when found replace
		}
		// traverse
		if (ptr.firstChild!=null){
			recReplaceTag(ptr.firstChild, oldTag, newTag);	
		}
		if (ptr.sibling!=null){
			recReplaceTag(ptr.sibling,oldTag,newTag);
		}
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		recBoldRow(root, row);
	}
	
	
	private void recBoldRow(TagNode ptr, int row){
		
		if(ptr.tag.equals("table")){
		TagNode nptr = ptr;
		nptr = nptr.firstChild;
		int count = 1;
		while(count != row){
		count++;
		nptr = nptr.sibling;
		}
		nptr = nptr.firstChild;
		while(nptr != null){
		TagNode bold = new TagNode("b", nptr.firstChild, null);
		nptr.firstChild = bold;
		nptr = nptr.sibling;
		}

		}
		
		else{
		if(ptr.sibling != null){
			recBoldRow(ptr.sibling, row);
		}
		if(ptr.sibling == null && ptr.firstChild != null){
			recBoldRow(ptr.firstChild, row);
		}
		}
		}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if((tag.equals("p") || tag.equals("em") || tag.equals("b"))) 
			removeTag1(root, tag);	
		else if(tag.equals("ol") || tag.equals("ul"))
			removeTag2(root, tag);
	}
	private void removeTag1(TagNode ptr, String tag) { 
		
		if(ptr == null) return;
		
		if(ptr.tag.equals(tag)) {
			
			ptr.tag = ptr.firstChild.tag;
			
			if(ptr.firstChild.sibling != null) {
				
				TagNode rear = ptr.firstChild;
				TagNode temp = ptr.sibling;
				ptr.sibling=rear.sibling;
				ptr.firstChild = null;
				while(ptr.sibling != null){
					ptr = ptr.sibling;
				}
				ptr.sibling = temp;
			}
			else{
				ptr.firstChild = null;
			}
		}
		if(ptr.firstChild != null)
			removeTag1(ptr.firstChild, tag); 
		if(ptr.sibling != null)
			removeTag1(ptr.sibling, tag);
	}
	
	private void removeTag2(TagNode ptr, String tag){
		TagNode rear = null;
		if(ptr.tag.equals(tag)){
			
		for(rear = ptr.firstChild; rear != null; rear = rear.sibling){
			rear.tag = "p";
		}
		
		ptr.tag = ptr.firstChild.tag;
		
		if(ptr.firstChild.sibling != null) {
			
			TagNode nptr = ptr.firstChild;
			TagNode temp = ptr.sibling;
			ptr.sibling = nptr.sibling;
			ptr.firstChild = nptr.firstChild;
			while(ptr.sibling != null){
				ptr = ptr.sibling;
			}
			ptr.sibling = temp;
		}
		}
		
		if(ptr.firstChild != null){
			removeTag2(ptr.firstChild, tag); 
		}
		if(ptr.sibling != null){
			removeTag2(ptr.sibling, tag);
		}
	}
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		recAddTag (root, word, tag);
	}
	
// Check if string is punctuation 
	private boolean isPunctuation(String s){
		if (s.charAt(s.length()-1) == '.' || s.charAt(s.length()-1) == '!' || s.charAt(s.length()-1) == '?' || 
				s.charAt(s.length()-1) == ',' || s.charAt(s.length()-1) == ';' || s.charAt(s.length()-1) == ':'){
			return true;
		}
		else return false;
	}
	
	
	private void recAddTag(TagNode ptr, String word, String tag){

		if (ptr.tag.toLowerCase().contains(word.toLowerCase())){
			boolean canTag = false;
	
			String[] arr = ptr.tag.split(" ");
			
				for (int i = 0; i < arr.length; i++){
				  // check can tag
					canTag = false;
					
					if (arr[i].toLowerCase().contains(word.toLowerCase())){
						if ((arr[i].length() - word.length()) == 1 && arr[i].indexOf(word) == 0){
							if (isPunctuation(arr[i])){
							canTag = true;
							}
						}
						if ((arr[i].length() - word.length()) == 0){
							canTag = true;
						}
					//done check can tag
						if (canTag == true){
							String target = arr[i];
							if (arr.length == 1){ 
								
								TagNode tempchild = new TagNode(target, null, null);
								ptr.tag = tag;
								ptr.firstChild = tempchild;
								ptr = ptr.firstChild;
								
							}
							else{
							
								if (i == 0){
									String after = ptr.tag.substring(target.length());
									TagNode tempsibling = ptr.sibling;
									ptr.tag = tag;
									ptr.firstChild = new TagNode(target, null, null);
									ptr.sibling = new TagNode(after, null, tempsibling); 
									TagNode nptr = ptr;
									while (!nptr.tag.equals(after)){
										nptr = nptr.sibling;
									}
									ptr = nptr;
					
								}
								else if (i > 0 && i < arr.length-1){
									int x = ptr.tag.indexOf(target);
									
									String before = ptr.tag.substring(0, x);
									
									String after = ptr.tag.substring((x+target.length()), ptr.tag.length());	
									
									TagNode tempchild = ptr.firstChild;
									TagNode tempsibling = ptr.sibling;
									
									ptr.tag = before;
									ptr.sibling = new TagNode(tag, null, null);
									TagNode temptarget = ptr.sibling;
									temptarget.firstChild = new TagNode(target,null, null);
									temptarget.sibling = new TagNode(after, tempchild, tempsibling);
									
									TagNode nptr = ptr;
									while (!nptr.tag.equals(after)){
										nptr = nptr.sibling;
									}
									ptr = nptr;
								}
								else{ 
									String before = ptr.tag.substring(0, (ptr.tag.length()-target.length()));
									TagNode tempsibling = ptr.sibling;
									ptr.tag = before;
									ptr.sibling = new TagNode(tag, null, tempsibling);
									TagNode temptarget = ptr.sibling;
									temptarget.firstChild = new TagNode(target, null, null);
									TagNode nptr = ptr;
									
									  while (!nptr.tag.equals(tag)){
										nptr = nptr.sibling;
									}
									  if (nptr.sibling != null){
										  nptr = nptr.sibling;
										  ptr = nptr;
									  }
									  else{
									nptr = nptr.firstChild;
									ptr = nptr; 
									  }
									
								}	
							}
					}
					}
				}
			
		}
		if (ptr.firstChild != null){
			recAddTag(ptr.firstChild, word, tag);
		}
		if (ptr.sibling != null){
			recAddTag(ptr.sibling, word, tag);
		}
		
	}
	
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
