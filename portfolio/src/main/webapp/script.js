// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/******
 *** Basic Page Style/CSS-Related Functions
 *****/

/**
Sets the header for every page.
 */
function getHeader() {
    document.getElementById("header").innerHTML += `<p class="header-text"> Shuli Jones </p>
<div id="navbar">
    <a href="index.html" class="navlink">Home</a> 
    <div class="dropdown">
        <a href="projects.html" class="navlink">Projects</a>
        <div class="dropdown-list">
            <a href="projects.html#tm" onclick="showMore('tm')">Task Manager</a>
            <a href="projects.html#ma" onclick="showMore('ma')">Matching Algorithm </a>
            <a href="projects.html#l" onclick="showMore('l')">Loft Bed</a>
        </div>
    </div> 
    <div class="dropdown">
        <a href="elsewhere.html" class="navlink">Elsewhere</a>
        <div class="dropdown-list">
            <a href="https://github.com/shulijones" target="_blank">GitHub</a>
            <a href="https://mitadmissions.org/blogs/author/jonsh/"
            target="_blank">MIT Admissions</a>
        </div>
    </div>
</div>`;
}

/**
 * Shows a section of hidden text (e.g. after a user clicks to indicate they want more information).
 * If the user clicks again, toggles to hide the text.
 * This only works when already on the page with the hidden text, not from 
 another page (since then the element doesn't yet exist).
 */
function showMore(text) {
  var element = document.getElementById(`more-info--${text}`);
  
  if (element.style.maxHeight === '0px' || element.style.maxHeight === '') {
    element.style.maxHeight = '300px'; 
    /* Magic number to get around the fact that you can't animate 
       height to move from 0 to auto; all elements have maxHeight < 300px */
  }
  else {
    element.style.maxHeight = '0px';
  }
}

/**
 * Checks if the user is at an anchor for a particular section of hidden text,
 * and reveals it if they are.
 */
function checkShowMore() {
  if (window.location.href.includes('#')) { /* At an anchor */
    let text = window.location.href.split('#').pop() /* The anchor's name */
    showMore(text);

    /* If the user got to the anchor via a dropdown, the focus is now on that
    dropdown link. But it should be on the hidden text itself instead, so we'll 
    move it. */
    document.getElementById(text).focus();
  }
}


/**
 * Makes a note of when a user first presses tab. This indicates that they are
 * a keyboard user rather than a mouse user, so we'll turn on the focus rings 
 * for them to see where the tab focus is. 
 */
function handleFirstTab(e) {
    if (e.keyCode === 9) { // the "I am a keyboard user" key
        document.body.classList.add('user-is-tabbing');
        window.removeEventListener('keydown', handleFirstTab);
        /* Don't have to keep track of if they pressed tab anymore,
           since we already know they're a keyboard user */
    }
}

/******
 *** Guestbook-Related Functions
 *****/

/**
 * Loads the guest book comments and images. 
 */
function getGuestBook() {
  getGuestBookComments();
  getGuestBookImages();
}

/**
 * Loads the guest book comments from Datastore.
 */
function getGuestBookComments() {
  const guestBook = document.getElementById('guest-book-comments');
  guestBook.innerHTML= '' //Remove any pre-existing comments
  const maxComments = document.getElementById('comments-num').value;
  const lang = document.getElementById('language').value;
  const url = '/guestbook?max-comments=' + maxComments.toString() +
    '&lang='  + lang;

  fetch(url).then(response => response.json()).then((comments) => {
    comments.forEach((comment) => {
      guestBook.appendChild(createComment(comment, lang));
      guestBook.appendChild(document.createElement('br')); 
        /* Add break before next comment - one extra at the end is actually
        good, makes it easier to scroll down to read the last comment */
    });
  })
}

/**
 * Loads the guest book images from Datastore and displays them using Blobstore.
 */
function getGuestBookImages() {
  const guestBook = document.getElementById('guest-book-images');
  guestBook.innerHTML= '' //Remove any pre-existing images

  fetch("/image-handler").then(response => response.json())
  .then((images) => { 
    images.forEach((image) => {
      const imageHtml = document.createElement('img');
      const author = document.createElement('p');

      imageHtml.src = "/image-server?blob-key=" + image.blobKey;
      imageHtml.width = 300;
      author.innerText = "Submitted by " + image.author;
      guestBook.appendChild(imageHtml);
      guestBook.appendChild(author);
      guestBook.appendChild(document.createElement('br'));
    }); 
  })
}

/**
 * Creates a guestbook comment in the form of an HTML div element.
 */
function createComment(comment, lang) {
  const commentHolder = document.createElement('div');
  const commentText = document.createElement('p');
  const commentSignature = document.createElement('p');
  
  commentHolder.className = 'comment';
  commentText.className = 'message';
  commentSignature.className = 'signature';

  commentText.innerText = comment.text; 
  commentSignature.innerText = comment.author + " (" + 
    formatAndTranslateDate(comment.timestamp, lang) + ")";

  commentHolder.appendChild(commentText);
  commentHolder.appendChild(commentSignature);
  return commentHolder;
}

/**
 * Takes a date in epoch milliseconds and returns it in the string
 * format MonthName DayNumber, YearNumber (e.g. 'January 1, 1970'),
 * where the month name has been translated into the language lang.
 * lang should be a two-letter ISO 639 code.
 */
function formatAndTranslateDate(epochDate, lang) {
  const dateTimeFormat = new Intl.DateTimeFormat(lang, 
    { year: 'numeric', month: 'long', day: 'numeric' }) 
  const [{ value: month },,{ value: day },,{ value: year }] = dateTimeFormat 
    .formatToParts(new Date(epochDate) );
  return `${month} ${day}, ${year}`;
}

/**
 * Deletes all guestbook comments from the website if the 
 * correct password is entered.
 */
function deleteData() {
  const enteredPassword = document.getElementById('pwd').value;
  const requestURL = '/delete-data?password=' + enteredPassword;
  const request = new Request(requestURL, {method: 'POST'});
  
  fetch(request).then((response) => {
    const passwordMessage = document.getElementById("password-fail");
    if (response.ok) {
      passwordMessage.innerText = '';
      getGuestBook();
      return;
    }
    //Otherwise: some error was returned
    response.json().then((jsonResponse) => {
      if ('errorMessage' in jsonResponse) {
        passwordMessage.innerText = jsonResponse.errorMessage; 
      }
      else {
        passwordMessage.innerText = 
          `Unknown error occurred:
            ${response.status}: ${response.statusText}`;
      }
    });
  });
}

/**
 * Loads the blobstore image storage and shows the form to submit an image.
 */
function loadBlobstoreAndForm() {
  fetch('/blobstore-upload-url')
      .then((response) => response.text())
      .then((uploadUrl) => { 
        const messageForm = document.getElementById('image-submit');
        //Make it so that submitting the image form will redirect to Blobstore's upload URL
        messageForm.action = uploadUrl; 
        messageForm.classList.remove('hidden'); //Show image form now that it's ready
      });
}

/**
 * Adds a random fact to the page. Deprecated.
 */
function addRandomFact() {
  const facts =
      ['No one knows exactly how many languages there are, \
      but the number is estimated to be between 6 and 8 thousand.',
       'Tetris is NP-complete.', 
       'The youngest person ever to swim across the English channel \
       was a 17-year-old Canadian named Marilyn Bell.', 
       'The first video ever played on MTV was the music video \
       for the song "Video Killed the Radio Star", by the Buggles.'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}
