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
    /* Magic number to get around the fact that you can't animate to auto */
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
  if (window.location.href.includes("#")) {
    let text = window.location.href.split("#").pop()
    showMore(text);

    /*If the user got to the anchor via a dropdown, the focus is now on that
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
    }
}

/**
 * Loads guest book comments.
 */
function getGuestBook() {
  const guestBook = document.getElementById('guest-book-comments');
  fetch("/data").then(response => response.json()).then((comments) => {
    comments.forEach((comment) => {
      const commentHolder = document.createElement('div');
      const commentText = document.createElement('p');
      const commentAuthor = document.createElement('p');
      
      commentHolder.className = "comment";
      commentText.className = "message";
      commentAuthor.className = "author";

      commentText.innerText = comment.text; 
      commentAuthor.innerText = comment.author;

      commentHolder.appendChild(commentText);
      commentHolder.appendChild(commentAuthor);
      guestBook.appendChild(commentHolder);
      guestBook.appendChild(document.createElement('br'));
    });
  })
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
